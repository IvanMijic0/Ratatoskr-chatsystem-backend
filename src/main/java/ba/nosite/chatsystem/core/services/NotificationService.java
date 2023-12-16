package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.services.redisServices.RedisSetService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final RedisSetService redisSetService;

    public NotificationService(RedisSetService redisSetService) {
        this.redisSetService = redisSetService;
    }

    public void addNotification(String userId, String notification) {
        String key = "notifications:".concat(userId);
        redisSetService.addToSet(key, notification);
    }

    public Set<String> getNotifications(String userId, Class<?> clazz) {
        String key = "notifications:".concat(userId);

        Set<Object> notifications = redisSetService.getSet(key, clazz);

        return notifications.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void removeNotification(String userId, String notification) {
        String key = "notifications:".concat(userId);
        redisSetService.removeFromSet(key, notification);
    }
}
