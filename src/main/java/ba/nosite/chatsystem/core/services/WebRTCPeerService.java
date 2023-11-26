package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.WebRTCMessage;
import ba.nosite.chatsystem.core.models.chat.WebRTCPeer;
import ba.nosite.chatsystem.core.repository.WebRTCPeerRepository;
import org.springframework.stereotype.Service;

@Service
public class WebRTCPeerService {
    private final WebRTCPeerRepository webRTCPeerRepository;

    public WebRTCPeerService(WebRTCPeerRepository webRTCPeerRepository) {
        this.webRTCPeerRepository = webRTCPeerRepository;
    }

    public void createWebRTCPeer(String username, String sessionId) {
        WebRTCPeer webRTCPeer = new WebRTCPeer(
                username,
                sessionId
        );
        webRTCPeerRepository.save(webRTCPeer);
    }

    public WebRTCPeer findByUsername(String username) {
        return webRTCPeerRepository.findByUsername(username);
    }

    public void registerPeer(String username, String sessionId) {
        WebRTCPeer existingPeer = findByUsername(username);
        if (existingPeer != null) {
            existingPeer.setSessionId(sessionId);
            webRTCPeerRepository.save(existingPeer);
        } else {
            createWebRTCPeer(username, sessionId);
        }
    }

    public void sendMessageToPeer(String recipientUsername, WebRTCMessage message) {
        WebRTCPeer recipient = webRTCPeerRepository.findByUsername(recipientUsername);
        WebRTCPeer sender = webRTCPeerRepository.findByUsername(message.getSender());

        if (recipient != null && sender != null) {
            //   recipient.sendWebRTCMessage(message);

            System.out.println(
                    "Sent WebRTC message from "
                            .concat(message.getSender())
                            .concat(" to ")
                            .concat(recipientUsername)
                            .concat(": ")
                            .concat(message.getType())
            );
        }
    }
}
