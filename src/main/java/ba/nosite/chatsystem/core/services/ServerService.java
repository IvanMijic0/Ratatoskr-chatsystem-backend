package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.chatDtos.Channel;
import ba.nosite.chatsystem.core.dto.chatDtos.ChannelClusterInfo;
import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.models.chat.ChannelCluster;
import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import ba.nosite.chatsystem.core.models.chat.MessageType;
import ba.nosite.chatsystem.core.models.chat.Server;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.ServerRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import ba.nosite.chatsystem.customTypes.Tuple2;
import com.amazonaws.services.kms.model.NotFoundException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static ba.nosite.chatsystem.core.services.authServices.JwtService.extractJwtFromHeader;

@Service
public class ServerService {
    private final ServerRepository serverRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final AwsS3ImageService awsS3ImageService;
    Logger logger;

    public ServerService(
            ServerRepository serverRepository,
            JwtService jwtService,
            UserService userService,
            AwsS3ImageService awsS3ImageService
    ) {
        this.serverRepository = serverRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.awsS3ImageService = awsS3ImageService;
        logger = LoggerFactory.getLogger(ServerService.class);
    }

    public List<ChannelClusterInfo> findChannelClustersByServerId(String serverId) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);

        return serverOptional.map(server ->
                        server.getChannelClusters()
                                .stream()
                                .map(channelCluster -> new ChannelClusterInfo(
                                        channelCluster.get_id(),
                                        channelCluster.getName(),
                                        getChannelInfosWithoutChatMessages(channelCluster.getChannels())
                                ))
                                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public ServerInfoResponse findServerById(String serverId) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);

        return serverOptional.map(server -> new ServerInfoResponse(
                        server.get_id(),
                        server.getName(),
                        server.getAvatarIconUrl(),
                        server.getChannelClusters().getFirst().get_id(),
                        server.getChannelClusters().getFirst().getChannels().getFirst().get_id()
                ))
                .orElseThrow(() -> new NotFoundException("Server not found with ID: " + serverId));
    }


    public ba.nosite.chatsystem.core.models.chat.Channel getChannelByIds(String serverId, String channelClusterId, String channelId) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);

        return serverOptional.flatMap(server -> {
            Optional<ChannelCluster> channelClusterOptional = server.getChannelClusters()
                    .stream()
                    .filter(channelCluster -> channelCluster.get_id().equals(channelClusterId))
                    .findFirst();


            return channelClusterOptional.map(channelCluster -> channelCluster.getChannels()
                    .stream()
                    .filter(channel -> channel.get_id().equals(channelId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Channel not found with ID: ".concat(channelId))));
        }).orElseThrow(() -> new NotFoundException("Server not found with ID: ".concat(serverId)));
    }


    public void saveServer(String serverName, String authHeader, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        Tuple2<String, Date> result = awsS3ImageService.uploadImage(file);
        String fileUrl = result.getFirst();
        Date expirationTime = result.getSecond();

        String ownerId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");
        User owner = userService.getUserById(ownerId);

        ChatMessage chatMessage = new ChatMessage(
                "Hello new User!",
                "Ratatoskr",
                MessageType.MESSAGE);

        ba.nosite.chatsystem.core.models.chat.Channel channel = new ba.nosite.chatsystem.core.models.chat.Channel("General", new ArrayList<>(List.of(chatMessage)));

        ChannelCluster channelCluster = new ChannelCluster("Defaults", new ArrayList<>(List.of(channel)));

        Server server = new Server(
                serverName,
                ownerId,
                new ArrayList<>(List.of(owner)),
                new ArrayList<>(List.of(channelCluster)),
                fileUrl,
                expirationTime
        );
        serverRepository.save(server);
    }

    public List<ServerInfoResponse> findServerByMemberId(String authHeader) {
        String memberId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");

        List<Server> servers = serverRepository.findServersByMemberId(new ObjectId(memberId))
                .orElseThrow(() -> new NotFoundException("This user is not a member in any server!"));

        return servers.stream()
                .map(server -> {
                    String avatarIconUrl = server.getAvatarIconUrl();
                    Tuple2<String, Date> result = awsS3ImageService.refreshAvatarIconUrl(avatarIconUrl);
                    if (result != null) {
                        logger.info("Refreshing avatar icon URL for - {}", avatarIconUrl);

                        String refreshedUrl = result.getFirst();
                        Date expirationTime = result.getSecond();

                        server.setAvatarIconUrl(refreshedUrl);
                        server.setAvatarIconUrlExpirationTime(expirationTime);

                        serverRepository.save(server);
                    }

                    ChannelCluster channelCluster = server.getChannelClusters().getFirst();
                    ba.nosite.chatsystem.core.models.chat.Channel channel = channelCluster.getChannels().getFirst();

                    String finalAvatarIconUrl = result != null ? result.getFirst() : avatarIconUrl;

                    return new ServerInfoResponse(
                            server.get_id(),
                            server.getName(),
                            finalAvatarIconUrl,
                            channelCluster.get_id(),
                            channel.get_id()
                    );
                })
                .collect(Collectors.toList());
    }

    public void addChannelClusterToServer(String serverId, String channelClusterName) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);
        serverOptional.ifPresent(server -> {
            List<ChannelCluster> channelClusters = server.getChannelClusters();

            ChatMessage defaultChatMessage = new ChatMessage("Greetings, User!", "Ratatoskr", MessageType.MESSAGE);
            ba.nosite.chatsystem.core.models.chat.Channel defaultChannel = new ba.nosite.chatsystem.core.models.chat.Channel("General", new ArrayList<>(List.of(defaultChatMessage)));
            ChannelCluster channelCluster = new ChannelCluster(channelClusterName, new ArrayList<>(List.of(defaultChannel)));

            channelClusters.add(channelCluster);
            server.setChannelClusters(channelClusters);
            serverRepository.save(server);
        });
    }

    public void addChannelToCluster(String serverId, String clusterId, String channelName) {
        serverRepository.findById(serverId).ifPresent(server -> {
            List<ChannelCluster> channelClusters = server.getChannelClusters();
            ChatMessage defaultChatMessage = new ChatMessage("Greetings, User!", "Ratatoskr", MessageType.MESSAGE);
            ba.nosite.chatsystem.core.models.chat.Channel defaultChannel = new ba.nosite.chatsystem.core.models.chat.Channel(channelName, new ArrayList<>(List.of(defaultChatMessage)));

            for (ChannelCluster cluster : channelClusters) {
                if (cluster.get_id().equals(clusterId)) {
                    List<ba.nosite.chatsystem.core.models.chat.Channel> channels = cluster.getChannels();
                    channels.add(defaultChannel);
                    cluster.setChannels(channels);
                    break;
                }
            }
            serverRepository.save(server);
        });
    }

    public void deleteChannelsInCluster(String serverId, String clusterId, String[] channelIds) {
        Set<String> channelIdSet = new HashSet<>(Arrays.asList(channelIds));

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NotFoundException("Server not found with ID: ".concat(serverId)));

        server.getChannelClusters().forEach(cluster -> {
            if (cluster.get_id().equals(clusterId)) {
                List<ba.nosite.chatsystem.core.models.chat.Channel> channels = cluster.getChannels();

                channels = channels.stream()
                        .filter(channel -> !channelIdSet.contains(channel.get_id()))
                        .collect(Collectors.toList());

                cluster.setChannels(channels);
            }
        });
        serverRepository.save(server);
    }

    public void deleteChannelClusterById(String serverId, String clusterId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NotFoundException("Server not found with ID: ".concat(serverId)));

        List<ChannelCluster> channelClusters = server.getChannelClusters();
        channelClusters.removeIf(cluster -> cluster.get_id().equals(clusterId));

        server.setChannelClusters(channelClusters);
        serverRepository.save(server);
    }

    public void deleteServerById(String serverId) {
        Server server = serverRepository.findById(serverId).orElse(null);

        if (server != null) {
            String imageUrl = server.getAvatarIconUrl();
            deleteImageFromS3(imageUrl);

            serverRepository.deleteById(serverId);
        }
    }

    private void deleteImageFromS3(String imageUrl) {
        awsS3ImageService.deleteImage(imageUrl);
    }

    private List<Channel> getChannelInfosWithoutChatMessages(List<ba.nosite.chatsystem.core.models.chat.Channel> channels) {
        return channels.stream()
                .map(channel -> new Channel(channel.get_id(), channel.getName()))
                .collect(Collectors.toList());
    }
}