package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.chat.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWSController {
    @MessageMapping("/notifications/{receiverId}/friendRequest.send")
    @SendTo("/notifications/{receiverId}")
    public Notification sendNotification(
            @Payload Notification notification) {
        return notification;
    }

    @MessageMapping("/notifications/online")
    @SendTo("/notifications/onlineStatus")
    public String sendOnlineStatus(@Payload String userName) {
        return userName;
    }
}