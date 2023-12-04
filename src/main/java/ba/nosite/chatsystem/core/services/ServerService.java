package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.models.chat.*;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.ServerRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
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


            if (expirationTime != null && expirationTime.getTime() > currentTime) {
                avatarIconUrlCreationTimes.put(server.getAvatarIconUrl(), expirationTime.getTime());
            }
        }
    }

    public List<ChannelCluster> findChannelsByServerId(String serverId) {
        Optional<Server> serverOptional = serverRepository.findById(serverId);
        return serverOptional.map(Server::getChannelClusters).orElse(Collections.emptyList());
    }

    public void saveServer(String serverName, String authHeader, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName = UUID
                .randomUUID()
                .toString()
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
                            String fileName = extractFileNameFromUrl(avatarIconUrl);
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


    private boolean isUrlExpired(String url) {
        if (avatarIconUrlCreationTimes.isEmpty()) {
            return false;
        }

        Long creationTime = avatarIconUrlCreationTimes.get(url);

        if (creationTime != null) {
            return System.currentTimeMillis() - creationTime >= s3ImageExpirationThresholdInMs;
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
