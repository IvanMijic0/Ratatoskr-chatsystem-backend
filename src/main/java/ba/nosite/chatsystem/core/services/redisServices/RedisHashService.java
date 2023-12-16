package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisHashService {
    private final JsonService jsonService;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final Logger logger = LoggerFactory.getLogger(RedisHashService.class);

    @Autowired
    public RedisHashService(JsonService jsonService, RedisTemplate<String, String> redisTemplate) {
        this.jsonService = jsonService;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public <T> void put(String key, String hashKey, T value) {
        try {
            hashOperations.put(key, hashKey, jsonService.toJson(value));
        } catch (Exception e) {
            logger.error("Error putting data into Redis hash: {}", e.getMessage());
        }
    }

    public <T> T get(String key, String hashKey, Class<T> clazz) {
        try {
            String jsonValue = hashOperations.get(key, hashKey);
            return jsonService.fromJson(jsonValue, clazz);
        } catch (Exception e) {
            logger.error("Error getting data from Redis hash: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, String> getAll(String key) {
        try {
            return hashOperations.entries(key);
        } catch (Exception e) {
            logger.error("Error getting all data from Redis hash: {}", e.getMessage());
            return null;
        }
    }

    public void delete(String key, String hashKey) {
        try {
            hashOperations.delete(key, hashKey);
        } catch (Exception e) {
            logger.error("Error deleting data from Redis hash: {}", e.getMessage());
        }
    }

    public void deleteAll(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            logger.error("Error deleting all data from Redis hash: {}", e.getMessage());
        }
    }
}
