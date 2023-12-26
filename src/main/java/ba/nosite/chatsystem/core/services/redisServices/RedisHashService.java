package ba.nosite.chatsystem.core.services.redisServices;

import ba.nosite.chatsystem.core.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class RedisHashService {
    private final JsonService jsonService;
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final Logger logger;
    @Value("${redis.dataMigration.expirationInDays}")
    private int expirationInDays;

    public RedisHashService(JsonService jsonService, RedisTemplate<String, String> redisTemplate) {
        this.jsonService = jsonService;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.logger = LoggerFactory.getLogger(RedisHashService.class);
    }

    public <T> void putWithoutExpire(String key, String hashKey, T value) {
        try {
            hashOperations.put(key, hashKey, jsonService.toJson(value));
        } catch (Exception e) {
            throw new RuntimeException("Error putting data into Redis hash", e);
        }
    }

    public <T> void put(String key, String hashKey, T value) {
        put(key, hashKey, value, Duration.ofDays(expirationInDays));
    }

    public <T> void put(String key, String hashKey, T value, Duration expiration) {
        try {
            hashOperations.put(key, hashKey, jsonService.toJson(value));
            redisTemplate.expire(key, expiration);
        } catch (Exception e) {
            throw new RuntimeException("Error putting data into Redis hash", e);
        }
    }

    public <T> T get(String key, String hashKey, Class<T> clazz) {
        try {
            String jsonValue = hashOperations.get(key, hashKey);
            return jsonService.fromJson(jsonValue, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error getting data from Redis hash", e);
        }
    }

    public Map<String, String> getAll(String key) {
        try {
            return hashOperations.entries(key);
        } catch (Exception e) {
            throw new RuntimeException("Error getting all data from Redis hash", e);
        }
    }

    public void delete(String key, String hashKey) {
        try {
            logger.info("Deleting hashKey {} from hash {}", hashKey, key);
            hashOperations.delete(key, hashKey);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting data from Redis hash", e);
        }
    }

    public void deleteAll(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting all data from Redis hash", e);
        }
    }

    public long size(String key) {
        try {
            return hashOperations.size(key);
        } catch (Exception e) {
            throw new RuntimeException("Error getting size of Redis hash", e);
        }
    }
}
