package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
