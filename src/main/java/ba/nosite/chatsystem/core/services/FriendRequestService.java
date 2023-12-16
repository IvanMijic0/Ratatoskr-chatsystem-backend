package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.services.redisServices.RedisListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendRequestService {
    private final RedisListService redisListService;

    public FriendRequestService(RedisListService redisListService) {
        this.redisListService = redisListService;
    }

    public void sendFriendRequest(String userId, String friendId) {
        String key = "friendRequests:".concat(userId);
        redisListService.addToList(key, friendId);
    }

    public List<Object> getFriendRequests(String userId, Class<?> clazz) {
        String key = "friendRequests:".concat(userId);
        return redisListService.getList(key, clazz);
    }

    public void removeFriendRequest(String userId, String friendId) {
        String key = "friendRequests:".concat(userId);
        redisListService.removeFromList(key, friendId);
    }
}
