package ba.nosite.chatsystem.core.models.chat;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebRTCMessageTest {

    @Test
    void shouldCreateWebRTCMessageWithSDP() {
        String type = "offer";
        String sender = "alice";
        String recipient = "bob";
        String sdp = "sdp_content";

        WebRTCMessage webRTCMessage = new WebRTCMessage(type, sender, recipient, sdp);

        assertEquals(type, webRTCMessage.getType());
        assertEquals(sender, webRTCMessage.getSender());
        assertEquals(recipient, webRTCMessage.getRecipient());
        assertEquals(sdp, webRTCMessage.getSdp());
    }

    @Test
    void shouldCreateWebRTCMessageWithIceCandidate() {
        String type = "ice";
        String sender = "bob";
        String recipient = "alice";

        Map<String, String> iceCandidate = new HashMap<>();
        iceCandidate.put("candidate", "candidate_content");
        iceCandidate.put("sdpMid", "sdp_mid");

        WebRTCMessage webRTCMessage = new WebRTCMessage(type, sender, recipient, iceCandidate);

        assertEquals(type, webRTCMessage.getType());
        assertEquals(sender, webRTCMessage.getSender());
        assertEquals(recipient, webRTCMessage.getRecipient());
        assertEquals(iceCandidate, webRTCMessage.getIceCandidate());
    }

    @Test
    void shouldSetAndGetIceCandidate() {
        WebRTCMessage webRTCMessage = new WebRTCMessage();

        Map<String, String> newIceCandidate = new HashMap<>();
        newIceCandidate.put("candidate", "new_candidate_content");
        newIceCandidate.put("sdpMid", "new_sdp_mid");

        webRTCMessage.setIceCandidate(newIceCandidate);

        assertEquals(newIceCandidate, webRTCMessage.getIceCandidate());
    }
}