package ba.nosite.chatsystem.core.models.chat;

import java.util.Date;

public class Notification {
    private NotificationType notificationType;
    private Date date;
    private String senderId;
    private String content;

    public Notification(NotificationType notificationType, Date date, String senderId, String content) {
        this.notificationType = notificationType;
        this.date = date;
        this.senderId = senderId;
        this.content = content;
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
