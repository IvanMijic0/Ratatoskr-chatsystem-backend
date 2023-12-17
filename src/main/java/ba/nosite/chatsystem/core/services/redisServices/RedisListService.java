package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class RedisListService {
    private final ListOperations<String, String> listOperations;
    private final JsonService jsonService;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${redis.dataMigration.expirationInDays}")
    private int expirationInDays;

    public RedisListService(RedisTemplate<String, String> redisTemplate, JsonService jsonService) {
        this.listOperations = redisTemplate.opsForList();
        this.jsonService = jsonService;
        this.redisTemplate = redisTemplate;
    }

    public void addToList(String key, Object value) {
        addToList(key, value, Duration.ofDays(expirationInDays));
    }

    public void addToList(String key, Object value, Duration expiration) {
        try {
            String jsonValue = jsonService.toJson(value);
            listOperations.leftPush(key, jsonValue);
            redisTemplate.expire(key, expiration);
        } catch (Exception e) {
            throw new RuntimeException("Error adding to Redis list", e);
        }
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            List<String> jsonValues = Objects.requireNonNull(listOperations.range(key, 0, -1));
            return jsonValues.stream()
                    .map(jsonValue -> jsonService.deserializeJsonValue(jsonValue, clazz))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error getting Redis list", e);
        }
    }

    public void removeFromList(String key, Object value) {
        try {
            String jsonValue = jsonService.toJson(value);
            listOperations.remove(key, 0, jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Error removing from Redis list", e);
        }
    }
}
