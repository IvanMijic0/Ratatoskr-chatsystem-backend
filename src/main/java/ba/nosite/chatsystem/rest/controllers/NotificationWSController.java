package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.services.NotificationService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWSController {
    private final NotificationService notificationService;

    public NotificationWSController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

//    @MessageMapping("/notifications.register")
//    @SendTo("/user/queue/notifications")
//    public void registerUserForNotifications(String userId, SimpMessageHeaderAccessor headerAccessor) {
//        String sessionId = headerAccessor.getSessionId();
//        notificationService.registerUserForNotifications(userId, sessionId);
//    }

    @MessageMapping("/notifications/{receiverId}/friendRequest.send")
    @SendTo("/notifications/{receiverId}")
    public Notification sendNotification(
            @DestinationVariable("receiverId") String receiverId,
            @Payload Notification notification) {


        System.out.println("Receiver ID: " + receiverId);
        System.out.println("Message Content: " + notification.getContent());

        return notification;
    }
}