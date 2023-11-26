package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.Server;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerRepository extends MongoRepository<Server, String> {

}
