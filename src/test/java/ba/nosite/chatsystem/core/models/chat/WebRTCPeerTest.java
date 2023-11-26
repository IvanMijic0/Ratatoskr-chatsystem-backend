package ba.nosite.chatsystem.core.models.chat;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebRTCPeerTest {

    @Test
    public void shouldCreateWebRTCPeer() {
        String username = "alice";
        String sessionId = "session123";

        WebRTCPeer webRTCPeer = new WebRTCPeer(username, sessionId);

        assertEquals(username, webRTCPeer.getUsername());
        assertEquals(sessionId, webRTCPeer.getSessionId());
    }

    @Test
    public void shouldSetAndGetId() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("alice", "session123");

        String newId = "newPeerId";
        webRTCPeer.setId(newId);

        assertEquals(newId, webRTCPeer.getId());
    }

    @Test
    public void shouldSetAndGetUsername() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("alice", "session123");

        String newUsername = "newAlice";
        webRTCPeer.setUsername(newUsername);

        assertEquals(newUsername, webRTCPeer.getUsername());
    }

    @Test
    public void shouldSetAndGetSessionId() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("alice", "session123");

        String newSessionId = "newSession456";
        webRTCPeer.setSessionId(newSessionId);

        assertEquals(newSessionId, webRTCPeer.getSessionId());
    }
}