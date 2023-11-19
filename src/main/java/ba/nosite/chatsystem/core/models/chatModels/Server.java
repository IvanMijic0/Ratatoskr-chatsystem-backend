package ba.nosite.chatsystem.core.models.chatModels;

import ba.nosite.chatsystem.core.models.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "server")
public class Server {
    private String name;
    @Indexed
    private String ownerId;
    @DBRef(lazy = true)
    private List<User> members;
    @Id
    private String id;
    @DBRef(lazy = true)
    private List<Channel> channels;

    public Server(String name, String ownerId, List<User> members, String id, List<Channel> channels) {
        this.name = name;
        this.ownerId = ownerId;
        this.members = members;
        this.id = id;
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
