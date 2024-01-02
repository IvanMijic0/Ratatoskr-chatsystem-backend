package ba.nosite.chatsystem.core.models.chat;

import org.springframework.data.annotation.Id;

public class WebRTCPeer {
    @Id
    private String id;
    private String username;
    private String sessionId;

    public WebRTCPeer(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
