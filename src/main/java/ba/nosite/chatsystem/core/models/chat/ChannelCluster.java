package ba.nosite.chatsystem.core.models.chat;

import java.util.List;
import java.util.UUID;

public class ChannelCluster {
    private String _id;
    private String name;

    private List<Channel> channels;

    public ChannelCluster(String name, List<Channel> channels) {
        this._id = UUID.randomUUID().toString();
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
