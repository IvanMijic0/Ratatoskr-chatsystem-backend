package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.services.redisServices.RedisListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {
    private final RedisListService redisListService;

    public ChatMessageService(RedisListService redisListService) {
        this.redisListService = redisListService;
    }

    public void saveChatMessage(String channelId, String message) {
        String key = "chatMessages:".concat(channelId);
        redisListService.addToList(key, message);
    }

    public List<String> getChatMessages(String channelId, Class<?> clazz) {
        String key = "chatMessages:".concat(channelId);
        List<Object> chatMessages = redisListService.getList(key, clazz);

        return chatMessages.stream()
                .map(String::valueOf)
                .toList();
    }
}
