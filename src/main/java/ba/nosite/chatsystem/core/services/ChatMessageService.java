package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.redisServices.RedisListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {
    private final RedisListService redisListService;
    private final UserService userService;

    public ChatMessageService(RedisListService redisListService, UserService userService) {
        this.redisListService = redisListService;
        this.userService = userService;
    }

    public void saveChatMessage(String channelId, String clusterId, String serverId, ChatMessage message) {
        String key = "chatMessages:".concat(clusterId).concat(":").concat(serverId).concat(":").concat(channelId);
        redisListService.addToList(key, message);
    }

    public List<ChatMessage> getChatMessages(String channelId, String clusterId, String serverId) {
        String key = "chatMessages:".concat(clusterId).concat(":").concat(serverId).concat(":").concat(channelId);
        return redisListService.getList(key, ChatMessage.class);
    }

    public void migrateChatMessagesToMongoDb() {
        List<User> allUsers = userService.listUsers();

        allUsers.forEach(this::migrateUserChatMessages);
    }

    private void migrateUserChatMessages(User user) {
        String userId = user.get_id();
        String keyPrefix = "chatMessages:";

    }
}
