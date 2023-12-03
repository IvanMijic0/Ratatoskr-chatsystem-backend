package ba.nosite.chatsystem.core.models.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "channelCluster")
public class ChannelCluster {
    @Id
    private String _id;
    private String name;

    @DBRef(lazy = true)
    private List<Channel> channels;

    public ChannelCluster(String name, List<Channel> channels) {
        this.name = name;
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

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
