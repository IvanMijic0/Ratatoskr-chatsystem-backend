package ba.nosite.chatsystem.core.models.chat;

import java.util.List;

public class Channel extends BaseChatEntity {
    private String name;

    public Channel(String name, List<ChatMessage> messages) {
        super(messages);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
