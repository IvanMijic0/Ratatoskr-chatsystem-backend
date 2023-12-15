package ba.nosite.chatsystem.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RedisListCache {
    private final ListOperations<String, Object> listOperations;
    Logger logger = LoggerFactory.getLogger(RedisListCache.class);

    public RedisListCache(RedisTemplate<String, Object> redisTemplate) {
        listOperations = redisTemplate.opsForList();
    }

    @PostConstruct
    public void setup() {
        listOperations.leftPush("testKey", "Redis works!");
        logger.info("Testing Redis key: ".concat(String.valueOf(listOperations.rightPop("testKey"))));
    }
}
