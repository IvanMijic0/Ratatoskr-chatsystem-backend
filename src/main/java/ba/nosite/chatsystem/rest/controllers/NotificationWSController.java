package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import ba.nosite.chatsystem.core.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWSController {
    private final NotificationService notificationService;
    private final Logger logger;

    public NotificationWSController(NotificationService notificationService) {
        this.notificationService = notificationService;
        logger = LoggerFactory.getLogger(NotificationWSController.class);
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

        logger.info("Receiver ID: {}", receiverId);
        logger.info("Message Content: {}", notification.getContent());

        return notification;
    }
}