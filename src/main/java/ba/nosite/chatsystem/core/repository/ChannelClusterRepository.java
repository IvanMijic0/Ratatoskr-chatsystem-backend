package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.ChannelCluster;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelClusterRepository extends MongoRepository<ChannelCluster, String> {
}
