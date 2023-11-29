package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.Channel;
import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import ba.nosite.chatsystem.core.models.chat.MessageType;
import ba.nosite.chatsystem.core.models.chat.Server;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.ServerRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static ba.nosite.chatsystem.helpers.jwtUtils.extractJwtFromHeader;

@Service
public class ServerService {
    private final ServerRepository serverRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final ChannelService channelService;
    private final ChatMessageService chatMessageService;
    private final AmazonS3 s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public ServerService(ServerRepository serverRepository, JwtService jwtService, UserService userService, ChannelService channelService, ChatMessageService chatMessageService, AmazonS3 s3Client) {
        this.serverRepository = serverRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.channelService = channelService;
        this.chatMessageService = chatMessageService;
        this.s3Client = s3Client;
    }

    public void saveServer(String serverName, String authHeader, MultipartFile file) throws IOException {
        String fileName = UUID
                .randomUUID()
                .toString()
                .concat("-")
                .concat(Objects.requireNonNull(file.getOriginalFilename()));

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), new ObjectMetadata()));
        String s3Url = "https://".concat(bucketName).concat(".s3.amazonaws.com/").concat(fileName);

        String ownerId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");
        User owner = userService.getUserById(ownerId);

        ChatMessage chatMessage = new ChatMessage("Hello new User!",
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
                s3Url
        );
        serverRepository.save(server);
    }
}
