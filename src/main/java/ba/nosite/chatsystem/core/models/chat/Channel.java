package ba.nosite.chatsystem.core.models.chat;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "channel")
public class Channel {
    private String _id;
    private String name;
    private List<ChatMessage> messages;

    public Channel(String name, List<ChatMessage> messages) {
        this._id = UUID.randomUUID().toString();
        this.name = name;
        this.messages = messages;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
