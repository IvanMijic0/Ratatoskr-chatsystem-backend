package ba.nosite.chatsystem.core.models.chat;

import java.util.List;

public class DirectMessaging extends BaseChatEntity {
    public DirectMessaging(List<ChatMessage> messages) {
        super(messages);
    }
}
