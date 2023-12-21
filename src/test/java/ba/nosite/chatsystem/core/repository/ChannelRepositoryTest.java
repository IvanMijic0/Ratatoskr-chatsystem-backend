package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.Channel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    public void shouldSaveChannel() {
        Channel channel = new Channel("channel123", "General", "server123", new ArrayList<>());
        channelRepository.save(channel);

        Optional<Channel> savedChannel = channelRepository.findById(channel.getId());
        Assertions.assertTrue(savedChannel.isPresent());
        Assertions.assertEquals("General", savedChannel.get().getName());
    }

    @Test
    public void shouldDeleteChannel() {
        Channel channel = new Channel("channelToDelete", "ToDelete", "server123", new ArrayList<>());
        channelRepository.save(channel);

        channelRepository.deleteById(channel.getId());

        Optional<Channel> deletedChannel = channelRepository.findById(channel.getId());
        Assertions.assertFalse(deletedChannel.isPresent());
    }

    @Test
    public void shouldUpdateChannel() {
        Channel channel = new Channel("channelToUpdate", "ToUpdate", "server123", new ArrayList<>());
        channelRepository.save(channel);

        channel.setName("UpdatedChannel");
        channelRepository.save(channel);

        Optional<Channel> updatedChannel = channelRepository.findById(channel.getId());
        Assertions.assertTrue(updatedChannel.isPresent());
        Assertions.assertEquals("UpdatedChannel", updatedChannel.get().getName());
    }

    @Test
    public void shouldFindAllChannels() {
        List<Channel> channels = channelRepository.findAll();
        Assertions.assertFalse(channels.isEmpty());
    }

    @Test
    public void shouldFindChannelById() {
        Channel channel = new Channel("channelById", "ById", "server123", new ArrayList<>());
        channelRepository.save(channel);

        Optional<Channel> foundChannel = channelRepository.findById(channel.getId());
        Assertions.assertTrue(foundChannel.isPresent());
        Assertions.assertEquals("ById", foundChannel.get().getName());
    }
}
