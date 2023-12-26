package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/{receiverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> addNotification(@PathVariable String receiverId, @RequestBody Notification notification) {
        notificationService.addNotification(receiverId, notification);
        return ResponseEntity.ok("Notification added successfully");
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Set<Notification>> getNotifications(@RequestHeader("Authorization") String authHeader) {
        Set<Notification> notifications = notificationService.getNotifications(authHeader);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> removeNotification(@RequestHeader("Authorization") String authHeader) {
        notificationService.removeNotification(authHeader);
        return ResponseEntity.ok("Notification removed successfully");
    }
}