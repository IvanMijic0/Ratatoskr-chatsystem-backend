package ba.nosite.chatsystem.core.dto.chatDtos;

import java.util.List;

public record ChannelClusterInfo(
        String id,
        String name,
        List<Channel> channels
) {
}
