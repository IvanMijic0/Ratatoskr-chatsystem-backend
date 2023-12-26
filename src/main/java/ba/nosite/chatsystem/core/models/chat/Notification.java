package ba.nosite.chatsystem.core.models.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Notification {
    private NotificationType notificationType;
    @JsonIgnore
    private Date date; // For now, I will ignore it...
    private String senderId;
    private String receiverId;
    private String content;

    public Notification() {
    }

    public Notification(NotificationType notificationType, Date date, String senderId, String content, String receiverId) {
        this.notificationType = notificationType;
        this.date = date;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
