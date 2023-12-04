package ba.nosite.chatsystem.core.models.chat;

import java.util.Date;

public class ChatMessage {
    private String senderName;
    private String content;
    private String receiverName;
    private Date date;
    private MessageType type;

    public ChatMessage() {
        // Default constructor
    }

    public ChatMessage(String senderName, String content, String receiverName, Date date, MessageType type) {
        this.senderName = senderName;
        this.content = content;
        this.receiverName = receiverName;
        this.date = date;
        this.type = type;
    }

    public ChatMessage(String content, String sender, MessageType type) {
        this.content = content;
        this.senderName = sender;
        this.type = type;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return senderName;
    }

    public void setSender(String sender) {
        this.senderName = sender;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
