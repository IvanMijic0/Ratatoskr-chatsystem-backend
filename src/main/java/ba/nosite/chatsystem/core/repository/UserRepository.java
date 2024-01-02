package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.dto.userDtos.UserInfo;
import ba.nosite.chatsystem.core.models.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{'$or':[ {'email': ?0}, {'username': ?0} ]}")
    Optional<User> findByEmailOrUsername(String emailOrUsername);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String code);

    @Aggregation(pipeline = {
            "{ $match: { 'username': { $regex: ?0, $options: 'i' }, '_id': { $ne: ?1 } } }",
            "{ $lookup: { from: 'user', localField: 'friends._id', foreignField: '_id', as: 'matchedFriends' } }",
            "{ $match: { 'matchedFriends': { $size: 0 } } }"
    })
    List<UserInfo> findByUsernameContainingIgnoreCaseAndNotCurrentUser(@Param("username") String username, @Param("currentUserId") String currentUserId);
}
