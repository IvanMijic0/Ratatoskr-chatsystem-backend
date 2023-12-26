package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisSetService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SetOperations<String, String> setOperations;
    private final JsonService jsonService;
    @Value("${redis.dataMigration.expirationInDays}")
    private int expirationInDays;

    public RedisSetService(RedisTemplate<String, String> redisTemplate, JsonService jsonService) {
        this.redisTemplate = redisTemplate;
        this.setOperations = redisTemplate.opsForSet();
        this.jsonService = jsonService;
    }

    public void addToSet(String key, Object value) {
        addToSet(key, value, Duration.ofDays(expirationInDays));
    }

    public void addToSet(String key, Object value, Duration expiration) {
        try {
            setOperations.add(key, jsonService.toJson(value));
            redisTemplate.expire(key, expiration);
        } catch (Exception e) {
            throw new RuntimeException("Error adding to Redis set", e);
        }
    }

    public <T> Set<T> getSet(String key, Class<T> clazz) {
        try {
            Set<String> jsonValues = Objects.requireNonNull(setOperations.members(key));
            return jsonValues.stream()
                    .map(jsonValue -> jsonService.deserializeJsonValue(jsonValue, clazz))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Error getting set from Redis", e);
        }
    }

    public void removeFromSet(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Error removing Redis set", e);
        }
    }
}