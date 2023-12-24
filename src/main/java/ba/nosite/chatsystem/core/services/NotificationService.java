package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.models.chat.NotificationType;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.redisServices.RedisSetService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {
    private final RedisSetService redisSetService;
    private final UserService userService;

    @Value("${redis.test.notifications.enabled}")
    private boolean testNotificationsEnabled;

    public NotificationService(RedisSetService redisSetService, UserService userService) {
        this.redisSetService = redisSetService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (testNotificationsEnabled) {
            addTestNotifications();
        }
    }

    public void addNotification(String userId, Notification notification) {
        String key = "notifications:".concat(userId);
        redisSetService.addToSet(key, notification);
    }

    public Set<Notification> getNotifications(String userId) {
        String key = "notifications:".concat(userId);
        return redisSetService.getSet(key, Notification.class);
    }

    public void removeNotification(String userId, Notification notification) {
        String key = "notifications:".concat(userId);
        redisSetService.removeFromSet(key, notification);
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

    private void addTestNotifications() {
        String userId = "testUser";
        Notification friendRequestNotification = new Notification(NotificationType.FRIEND_REQUEST, new Date(), "senderUserId", "Friend request content");
        Notification chatMessageNotification = new Notification(NotificationType.CHAT_MESSAGE, new Date(), "senderUserId", "New Chat Message content");

        addNotification(userId, friendRequestNotification);
        addNotification(userId, chatMessageNotification);
    }
}
