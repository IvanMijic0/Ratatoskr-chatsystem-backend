package ba.nosite.chatsystem.rest.configurations.webSocketConfigs;

import ba.nosite.chatsystem.core.models.enums.MessageType;
import ba.nosite.chatsystem.core.models.webSocketModels.ChatMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @EventListener
    public void HandleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        if (username != null) {
            System.out.println("User disconnected: ".concat(username));
            ChatMessage chatMessage = new ChatMessage(
                    "User: ".concat(username).concat(" has disconnected."),
                    username,
                    MessageType.LEAVE
            );

            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
