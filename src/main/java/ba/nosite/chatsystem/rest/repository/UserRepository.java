package ba.nosite.chatsystem.rest.repository;

import ba.nosite.chatsystem.rest.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {
}
