package ba.nosite.chatsystem.core.models.chat;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChatMessageTest {
    @Test
    void shouldCreateChatMessage() {
        String senderName = "Alice";
        String content = "Hello, Bob!";
        String receiverName = "Bob";
        Date date = new Date();
        MessageType type = MessageType.JOIN;

        ChatMessage chatMessage = new ChatMessage(senderName, content, receiverName, date, type);

        assertEquals(senderName, chatMessage.getSenderName());
        assertEquals(content, chatMessage.getContent());
        assertEquals(receiverName, chatMessage.getReceiverName());
        assertEquals(date, chatMessage.getDate());
        assertEquals(type, chatMessage.getType());
    }

    @Test
    void shouldCreateChatMessageWithoutReceiver() {
        String senderName = "Alice";
        String content = "Hello, everyone!";
        MessageType type = MessageType.JOIN;

        ChatMessage chatMessage = new ChatMessage(content, senderName, type);

        assertEquals(senderName, chatMessage.getSenderName());
        assertEquals(content, chatMessage.getContent());
        assertNull(chatMessage.getReceiverName());
        assertNull(chatMessage.getDate());
        assertEquals(type, chatMessage.getType());
    }
}
