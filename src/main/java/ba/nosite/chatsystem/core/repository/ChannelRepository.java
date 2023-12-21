package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelRepository extends MongoRepository<Channel, String> {
}
