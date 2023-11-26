package ba.nosite.chatsystem.core.models.chat;

import java.util.Map;

public class WebRTCMessage {
    private String type;
    private String sender;
    private String recipient;
    private String sdp;
    private Map<String, String> iceCandidate;

    public WebRTCMessage() {
    }

    public WebRTCMessage(String type, String sender, String recipient, String sdp) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.sdp = sdp;
    }

    public WebRTCMessage(String type, String sender, String recipient, Map<String, String> iceCandidate) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.iceCandidate = iceCandidate;
    }

    // Getters and setters for the fields

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public Map<String, String> getIceCandidate() {
        return iceCandidate;
    }

    public void setIceCandidate(Map<String, String> iceCandidate) {
        this.iceCandidate = iceCandidate;
    }
}
