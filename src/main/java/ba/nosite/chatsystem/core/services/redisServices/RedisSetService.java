package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisSetService {
    private final SetOperations<String, String> setOperations;
    private final JsonService jsonService;

    @Autowired
    public RedisSetService(RedisTemplate<String, String> redisTemplate, JsonService jsonService) {
        this.setOperations = redisTemplate.opsForSet();
        this.jsonService = jsonService;
    }

    public void addToSet(String key, Object value) {
        try {
            setOperations.add(key, jsonService.toJson(value));
        } catch (Exception e) {
            throw new RuntimeException("Error adding to Redis set", e);
        }
    }

    public Set<Object> getSet(String key, Class<?> clazz) {
        try {
            return Objects.requireNonNull(setOperations.members(key)).stream()
                    .map(jsonValue -> jsonService.deserializeJsonValue(jsonValue, clazz))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Error removing from Redis set", e);
        }
    }

    public void removeFromSet(String key, Object value) {
        try {
            setOperations.remove(key, jsonService.toJson(value));
        } catch (Exception e) {
            throw new RuntimeException("Error removing from Redis set", e);
        }
    }
}
