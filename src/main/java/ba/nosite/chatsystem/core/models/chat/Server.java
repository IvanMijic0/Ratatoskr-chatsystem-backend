package ba.nosite.chatsystem.core.models.chat;

import ba.nosite.chatsystem.core.models.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "server")
public class Server {
    @Id
    private String _id;
    private String name;
    @Indexed
    private String ownerId;
    @DBRef(lazy = true)
    private List<User> members;
    @DBRef(lazy = true)
    private List<Channel> channels;
    private String avatarIconUrl;

    public Server(String name, String ownerId, List<User> members, List<Channel> channels, String avatarIconUrl) {
        this.name = name;
        this.ownerId = ownerId;
        this.members = members;
        this.channels = channels;
        this.avatarIconUrl = avatarIconUrl;
    }

    public String getAvatarIconUrl() {
        return this.avatarIconUrl;
    }

    public void setAvatarIconUrl(String avatarIconUrl) {
        this.avatarIconUrl = avatarIconUrl;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
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
