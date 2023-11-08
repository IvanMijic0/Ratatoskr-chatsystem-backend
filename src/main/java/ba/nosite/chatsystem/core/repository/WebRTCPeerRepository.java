package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.webSocketModels.WebRTCPeer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebRTCPeerRepository extends MongoRepository<WebRTCPeer, String> {
    WebRTCPeer findByUsername(String username);
}