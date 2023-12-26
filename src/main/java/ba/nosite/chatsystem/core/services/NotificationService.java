package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import ba.nosite.chatsystem.core.services.redisServices.RedisSetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    public void addNotification(String receiverId, Notification notification) {
        String key = "notifications:".concat(receiverId);
        redisSetService.addToSet(key, notification);
    }

    public Set<Notification> getNotifications(String authHeader) {
        String userId = jwtService.extractCustomClaim(extractJwtFromHeader(authHeader), "user_id");

        String key = "notifications:".concat(userId);
        return redisSetService.getSet(key, Notification.class);
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
