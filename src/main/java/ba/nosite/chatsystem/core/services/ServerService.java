package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.models.chat.Channel;
import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import ba.nosite.chatsystem.core.models.chat.MessageType;
import ba.nosite.chatsystem.core.models.chat.Server;
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
    private final ChannelService channelService;
    private final ChatMessageService chatMessageService;
    private final AmazonS3 s3Client;
    private final Map<String, Long> avatarIconUrlCreationTimes;
    @Value("${aws.s3.expirationHours}")
    private Long s3ImageExpirationHours;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.expirationThresholdInMs}")
    private Long s3ImageExpirationThresholdInMs;

    public ServerService(ServerRepository serverRepository, JwtService jwtService, UserService userService, ChannelService channelService, ChatMessageService chatMessageService, AmazonS3 s3Client) {
        this.serverRepository = serverRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.channelService = channelService;
        this.chatMessageService = chatMessageService;
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


//    public String generateHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        byte[] hashInBytes = md.digest(file.getBytes());
//
//        StringBuilder hashStringBuilder = new StringBuilder();
//        for (byte b : hashInBytes) {
//            hashStringBuilder.append(String.format("%02x", b));
//        }
//        return hashStringBuilder.toString();
//    }

    public void saveServer(String serverName, String authHeader, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileName = UUID
                .randomUUID()
                .toString()
                .concat("-")
                .concat(Objects.requireNonNull(file.getOriginalFilename()));

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), new ObjectMetadata()));

        Date exp = generateExpirationTime();

        String fileUrl = generatePreSignedUrl(exp, fileName);

        String ownerId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");
        User owner = userService.getUserById(ownerId);

        ChatMessage chatMessage = new ChatMessage(
                "Hello new User!",
                "Ratatoskr",
                MessageType.MESSAGE);
        chatMessageService.saveChatMessage(chatMessage);

        Channel channel = new Channel("General", new ArrayList<>() {{
            add(chatMessage);
        }});
        channelService.saveChannel(channel);

        Server server = new Server(
                serverName,
                ownerId,
                new ArrayList<>() {{
                    add(owner);
                }},
                new ArrayList<>() {{
                    add(channel);
                }},
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

    private boolean isUrlExpired(String url) {
        return avatarIconUrlCreationTimes.containsKey(url) &&
                (System.currentTimeMillis() - avatarIconUrlCreationTimes.get(url) >= s3ImageExpirationThresholdInMs);
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
