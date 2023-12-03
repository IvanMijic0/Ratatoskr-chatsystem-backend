package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.Channel;
import ba.nosite.chatsystem.core.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void saveChannel(Channel channel) {
        channelRepository.save(channel);
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

}
