package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> addNotification(@PathVariable String userId, @RequestBody Notification notification) {
        notificationService.addNotification(userId, notification);
        return ResponseEntity.ok("Notification added successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Set<Notification>> getNotifications(@PathVariable String userId) {
        Set<Notification> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeNotification(@PathVariable String userId, @RequestBody Notification notification) {
        notificationService.removeNotification(userId, notification);
        return ResponseEntity.ok("Notification removed successfully");
    }
}
