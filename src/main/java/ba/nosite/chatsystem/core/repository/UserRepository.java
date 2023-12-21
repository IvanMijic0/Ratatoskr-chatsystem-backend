package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{'$or':[ {'email': ?0}, {'username': ?0} ]}")
    Optional<User> findByEmailOrUsername(String emailOrUsername);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String code);
}
