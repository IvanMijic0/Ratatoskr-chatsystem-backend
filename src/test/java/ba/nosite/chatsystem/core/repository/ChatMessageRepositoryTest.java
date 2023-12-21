package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import ba.nosite.chatsystem.core.models.chat.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void shouldSaveChatMessage() {
        ChatMessage chatMessage = new ChatMessage("Sender", "Hello, World!", "Receiver", null, MessageType.MESSAGE);
        chatMessageRepository.save(chatMessage);

        Optional<ChatMessage> savedMessage = chatMessageRepository.findById(chatMessage.get_id());
        Assertions.assertTrue(savedMessage.isPresent());
        Assertions.assertEquals("Hello, World!", savedMessage.get().getContent());
    }

    @Test
    public void shouldDeleteChatMessage() {
        ChatMessage chatMessage = new ChatMessage("Sender", "To be deleted", "Receiver", null, MessageType.MESSAGE);
        chatMessageRepository.save(chatMessage);

        chatMessageRepository.deleteById(chatMessage.get_id());

        Optional<ChatMessage> deletedMessage = chatMessageRepository.findById(chatMessage.get_id());
        Assertions.assertFalse(deletedMessage.isPresent());
    }

    @Test
    public void shouldFindAllChatMessages() {
        List<ChatMessage> messages = chatMessageRepository.findAll();
        Assertions.assertFalse(messages.isEmpty());
    }

    @Test
    public void shouldFindChatMessageById() {
        ChatMessage chatMessage = new ChatMessage("Sender", "To be found", "Receiver", null, MessageType.MESSAGE);
        chatMessageRepository.save(chatMessage);

        Optional<ChatMessage> foundMessage = chatMessageRepository.findById(chatMessage.get_id());
        Assertions.assertTrue(foundMessage.isPresent());
        Assertions.assertEquals("To be found", foundMessage.get().getContent());
    }
}
