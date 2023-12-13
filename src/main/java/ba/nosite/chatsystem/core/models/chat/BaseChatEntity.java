package ba.nosite.chatsystem.core.models.chat;

import java.util.List;
import java.util.UUID;

public class BaseChatEntity {
    private String _id;
    private List<ChatMessage> messages;

    public BaseChatEntity(List<ChatMessage> messages) {
        this._id = UUID.randomUUID().toString();
        this.messages = messages;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
