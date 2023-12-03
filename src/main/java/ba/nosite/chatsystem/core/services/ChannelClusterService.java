package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.chat.ChannelCluster;
import ba.nosite.chatsystem.core.repository.ChannelClusterRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelClusterService {
    private final ChannelClusterRepository channelClusterRepository;

    public ChannelClusterService(ChannelClusterRepository channelClusterRepository) {
        this.channelClusterRepository = channelClusterRepository;
    }

    public void save(ChannelCluster channelCluster) {
        channelClusterRepository.save(channelCluster);
    }
}
