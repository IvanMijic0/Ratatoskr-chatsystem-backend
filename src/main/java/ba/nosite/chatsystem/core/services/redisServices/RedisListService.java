package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RedisListService {
    private final ListOperations<String, String> listOperations;
    private final JsonService jsonService;

    @Autowired
    public RedisListService(RedisTemplate<String, String> redisTemplate, JsonService jsonService) {
        this.listOperations = redisTemplate.opsForList();
        this.jsonService = jsonService;
    }

    public void addToList(String key, Object value) {
        try {
            String jsonValue = jsonService.toJson(value);
            listOperations.leftPush(key, jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Error adding to Redis list", e);
        }
    }

    public List<Object> getList(String key, Class<?> clazz) {
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
