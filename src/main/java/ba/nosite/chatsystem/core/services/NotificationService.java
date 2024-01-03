package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.models.chat.NotificationType;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import ba.nosite.chatsystem.core.services.redisServices.RedisSetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static ba.nosite.chatsystem.core.services.authServices.JwtService.extractJwtFromHeader;

@Service
public class NotificationService {
    private final RedisSetService redisSetService;
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${redis.test.notifications.enabled}")
    private boolean testNotificationsEnabled;

    public NotificationService(RedisSetService redisSetService, UserService userService, JwtService jwtService) {
        this.redisSetService = redisSetService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void addNotification(String userId, Notification notification) {
        try {
            String key = "notifications:".concat(userId);
            Set<Notification> existingNotifications = redisSetService.getSet(key, Notification.class);

            if (existingNotifications == null) {
                existingNotifications = new HashSet<>();
            } else {
                boolean hasUserStatusChangedNotification = existingNotifications.stream()
                        .anyMatch(existingNotification -> existingNotification.getNotificationType() == NotificationType.USER_STATUS_CHANGED);

                if (hasUserStatusChangedNotification) {
                    existingNotifications.removeIf(existingNotification ->
                            existingNotification.getNotificationType() == NotificationType.USER_STATUS_CHANGED);
                }
            }

            existingNotifications.add(notification);
            redisSetService.setSet(key, existingNotifications);

        } catch (Exception e) {
            throw new RuntimeException("Error adding notification to Redis", e);
        }
    }


    public Set<Notification> getNotifications(String authHeader) {
        String userId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");

        String key = "notifications:".concat(userId);
        return redisSetService.getSet(key, Notification.class);
    }

    public Map<String, Set<Notification>> getNotificationsByUserIds(List<String> userIds) {
        Map<String, Set<Notification>> notificationsMap = new HashMap<>();

        userIds.forEach(userId -> {
            String key = "notifications:".concat(userId);
            Set<Notification> notifications = redisSetService.getSet(key, Notification.class);
            notificationsMap.put(userId, notifications);
        });

        return notificationsMap;
    }

    public void removeNotification(String authHeader) {
        String userId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");

        String key = "notifications:".concat(userId);
        redisSetService.removeFromSet(key);
    }

    public void migrateNotificationsToMongoDb() {
        List<User> allUsers = userService.listUsers();

        allUsers.forEach(this::migrateUserNotifications);
    }

    private void migrateUserNotifications(User user) {
        String userId = user.get_id();
        String key = "notifications:".concat(userId);

        Set<Notification> notifications = redisSetService.getSet(key, Notification.class);
        user.setNotifications(Set.copyOf(notifications));

        userService.save(user);
    }
}
