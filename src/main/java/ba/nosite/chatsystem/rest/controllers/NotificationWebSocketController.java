package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.services.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

    private final NotificationService notificationService;

    public NotificationWebSocketController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

//    @MessageMapping("/notifications.register")
//    @SendTo("/user/queue/notifications")
//    public void registerUserForNotifications(String userId, SimpMessageHeaderAccessor headerAccessor) {
//        String sessionId = headerAccessor.getSessionId();
//        notificationService.registerUserForNotifications(userId, sessionId);
//    }

    @MessageMapping("/notifications.send")
    @SendTo("/user/queue/notifications")
    public Notification sendNotification(Notification notification) {
        // Process the notification as needed
        return notification;
    }
}
