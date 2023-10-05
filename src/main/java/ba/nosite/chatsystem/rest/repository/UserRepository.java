package ba.nosite.chatsystem.rest.repository;

import ba.nosite.chatsystem.rest.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Integer> {
}
