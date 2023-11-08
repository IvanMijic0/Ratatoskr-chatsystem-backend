package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.models.webSocketModels.WebRTCMessage;
import ba.nosite.chatsystem.core.services.WebRTCPeerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebRTCController {
    private final WebRTCPeerService webRTCPeerService;

    public WebRTCController(WebRTCPeerService webRTCPeerService) {
        this.webRTCPeerService = webRTCPeerService;
    }

    @MessageMapping("/webrtc.register")
    @SendTo("/chatroom/public")
    public void registerPeer(String username, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        webRTCPeerService.registerPeer(username, sessionId);
    }

    @MessageMapping("/webrtc.sdp-offer")
    @SendTo("/chatroom/public")
    public void handleSDPOffer(WebRTCMessage message) {
        webRTCPeerService.sendMessageToPeer(message.getRecipient(), message);
    }

    @MessageMapping("/webrtc.signal")
    @SendTo("/webrtc/public")
    public WebRTCMessage signal(WebRTCMessage message) {
        return message;
    }

    @MessageMapping("/webrtc.ice-candidate")
    @SendTo("/chatroom/public")
    public void handleICECandidate(WebRTCMessage message) {
        webRTCPeerService.sendMessageToPeer(message.getRecipient(), message);
    }
}
