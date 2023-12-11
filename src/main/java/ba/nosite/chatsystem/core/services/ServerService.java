package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.chatDtos.ChannelClusterInfo;
import ba.nosite.chatsystem.core.dto.chatDtos.ChannelInfo;
import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.models.chat.*;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.ServerRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static ba.nosite.chatsystem.helpers.TimeConversion.convertHourToMs;
import static ba.nosite.chatsystem.helpers.jwtUtils.extractJwtFromHeader;

@Service
public class ServerService {
    private final ServerRepository serverRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final AmazonS3 s3Client;
    private final Map<String, Long> avatarIconUrlCreationTimes;
    Logger logger = LoggerFactory.getLogger(ServerService.class);
    @Value("${aws.s3.expirationHours}")
    private Long s3ImageExpirationHours;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.expirationThresholdInMs}")
    private Long s3ImageExpirationThresholdInMs;

    public ServerService(ServerRepository serverRepository, JwtService jwtService, UserService userService, AmazonS3 s3Client) {
        this.serverRepository = serverRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.s3Client = s3Client;
        avatarIconUrlCreationTimes = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        List<Server> servers = serverRepository.findAll();
        long currentTime = System.currentTimeMillis();

        for (Server server : servers) {
            Date expirationTime = server.getAvatarIconUrlExpirationTime();

            if (expirationTime != null && expirationTime.getTime() < currentTime) {
                avatarIconUrlCreationTimes.put(server.getAvatarIconUrl(), expirationTime.getTime());
            }
        }
    }

    public List<ChannelClusterInfo> findChannelClustersByServerId(String serverId) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);

        return serverOptional.map(server ->
                        server.getChannelClusters().stream()
                                .map(channelCluster -> new ChannelClusterInfo(
                                        channelCluster.get_id(),
                                        channelCluster.getName(),
                                        getChannelInfosWithoutChatMessages(channelCluster.getChannels())
                                ))
                                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public Channel getChannelByIds(String serverId, String channelClusterId, String channelId) {
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
                    .orElseThrow(() -> new NotFoundException("Channel not found with ID: " + channelId)));
        }).orElseThrow(() -> new NotFoundException("Server not found with ID: " + serverId));
    }


    public void saveServer(String serverName, String authHeader, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName = "server_images/"
                .concat(UUID.randomUUID().toString())
                .concat("-")
                .concat(Objects.requireNonNull(file.getOriginalFilename()));

        long contentLength = file.getSize();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

        Date exp = generateExpirationTime();

        String fileUrl = generatePreSignedUrl(exp, fileName);

        String ownerId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");
        User owner = userService.getUserById(ownerId);

        ChatMessage chatMessage = new ChatMessage(
                "Hello new User!",
                "Ratatoskr",
                MessageType.MESSAGE);

        Channel channel = new Channel("General", new ArrayList<>(List.of(chatMessage)));

        ChannelCluster channelCluster = new ChannelCluster("Defaults", new ArrayList<>(List.of(channel)));

        Server server = new Server(
                serverName,
                ownerId,
                new ArrayList<>(List.of(owner)),
                new ArrayList<>(List.of(channelCluster)),
                fileUrl,
                exp
        );
        serverRepository.save(server);
    }

    public List<ServerInfoResponse> findServerByMemberId(String authHeader) {
        String member_id = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");

        Optional<List<Server>> potential_servers = serverRepository.findServersByMemberId(new ObjectId(member_id));
        if (potential_servers.isPresent()) {
            List<Server> servers = potential_servers.get();
            Date newExpirationTime = generateExpirationTime();

            return servers
                    .stream()
                    .map(server -> {
                        String avatarIconUrl = server.getAvatarIconUrl();

                        if (isUrlExpired(avatarIconUrl)) {
                            logger.info("Refreshing avatar icon url for - ".concat(avatarIconUrl));
                            String fileName = "server_images/".concat(Objects.requireNonNull(extractFileNameFromUrl(avatarIconUrl)));
                            avatarIconUrl = generatePreSignedUrl(newExpirationTime, fileName);
                            server.setAvatarIconUrl(avatarIconUrl);
                            server.setAvatarIconUrlExpirationTime(newExpirationTime);

                            serverRepository.save(server);
                            avatarIconUrlCreationTimes.put(avatarIconUrl, newExpirationTime.getTime());
                        }

                        return new ServerInfoResponse(
                                server.get_id(),
                                server.getName(),
                                avatarIconUrl
                        );
                    })
                    .collect(Collectors.toList());
        }
        throw new NotFoundException("This user is not a member in any server!");
    }

    public void addChannelClusterToServer(String serverId, String channelClusterName) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);
        serverOptional.ifPresent(server -> {
            List<ChannelCluster> channelClusters = server.getChannelClusters();

            ChatMessage defaultChatMessage = new ChatMessage("Greetings, User!", "Ratatoskr", MessageType.MESSAGE);
            Channel defaultChannel = new Channel("General", new ArrayList<>(List.of(defaultChatMessage)));
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
            Channel defaultChannel = new Channel(channelName, new ArrayList<>(List.of(defaultChatMessage)));

            for (ChannelCluster cluster : channelClusters) {
                if (cluster.get_id().equals(clusterId)) {
                    List<Channel> channels = cluster.getChannels();
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
                .orElseThrow(() -> new NotFoundException("Server not found with ID: " + serverId));

        server.getChannelClusters().forEach(cluster -> {
            if (cluster.get_id().equals(clusterId)) {
                List<Channel> channels = cluster.getChannels();

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
                .orElseThrow(() -> new NotFoundException("Server not found with ID: " + serverId));

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
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String key = extractKeyFromImageUrl(imageUrl);
            System.out.println(key);

            try {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
                logger.info("Image deleted from S3 successfully.");
            } catch (Exception e) {
                logger.info("Error deleting image from S3: ".concat(e.getMessage()));
            }
        }
    }

    private String extractKeyFromImageUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (URISyntaxException e) {
            logger.error("Error parsing URL: ".concat(e.getMessage()));
            return null;
        }
    }

    private List<ChannelInfo> getChannelInfosWithoutChatMessages(List<Channel> channels) {
        return channels.stream()
                .map(channel -> new ChannelInfo(channel.get_id(), channel.getName()))
                .collect(Collectors.toList());
    }

    private boolean isUrlExpired(String url) {
        if (avatarIconUrlCreationTimes.isEmpty()) {
            return false;
        }
        Long creationTime = avatarIconUrlCreationTimes.get(url);

        if (creationTime != null) {
            return System.currentTimeMillis() - creationTime > s3ImageExpirationThresholdInMs;
        } else {
            logger.info("URL not found in the map: ".concat(url));

            avatarIconUrlCreationTimes.put(url, System.currentTimeMillis());
            return false;
        }
    }

    private String extractFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        int lastQuestionMarkIndex = url.lastIndexOf("?");
        if (lastSlashIndex != -1 && lastQuestionMarkIndex != -1 && lastSlashIndex < lastQuestionMarkIndex) {
            return url.substring(lastSlashIndex + 1, lastQuestionMarkIndex);
        }
        return null;
    }

    private String generatePreSignedUrl(Date exp, String fileName) {
        GeneratePresignedUrlRequest urlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(exp);

        return s3Client
                .generatePresignedUrl(urlRequest)
                .toString();
    }

    private Date generateExpirationTime() {
        Date exp = new Date();
        long exTimeMs = exp.getTime();

        exTimeMs += convertHourToMs(s3ImageExpirationHours);
        exp.setTime(exTimeMs);
        return exp;
    }
}
