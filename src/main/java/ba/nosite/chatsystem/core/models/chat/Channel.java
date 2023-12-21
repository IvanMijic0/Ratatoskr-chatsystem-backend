package ba.nosite.chatsystem.core.models.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "channel")
public class Channel {
    @Id
    private String id;
    private String name;
    @Indexed
    private String serverId;
    @DBRef(lazy = true)
    private List<ChatMessage> messages;

    public Channel(String id, String name, String serverId, List<ChatMessage> messages) {
        this.id = id;
        this.name = name;
        this.serverId = serverId;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
