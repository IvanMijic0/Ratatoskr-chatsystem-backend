package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.Server;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServerRepository extends MongoRepository<Server, String> {
    @Query(value = "{ 'members': { $elemMatch: { $ref: 'user', $id: ?0 } } }")
    Optional<List<Server>> findServersByMemberId(ObjectId memberId);
}