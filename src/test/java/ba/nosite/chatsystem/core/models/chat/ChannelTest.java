package ba.nosite.chatsystem.core.models.chat;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChannelTest {

    @Test
    void shouldCreateNewChannel() {
        Channel testChannel = new Channel(
                "General",
                Arrays.asList(
                        new ChatMessage("Hello, World!", "senderName1", "receiverName1", new Date(), MessageType.JOIN),
                        new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE)
                )
        );

        Assertions.assertEquals("General", testChannel.getName());
        Assertions.assertEquals(2, testChannel.getMessages().size());
    }

    @Test
    void shouldCompareTwoChannels() {
        Channel testChannel1 = new Channel(
                "channelId",
                Arrays.asList(
                        new ChatMessage("Hello, World!", "senderName1", "receiverName1", new Date(), MessageType.JOIN),
                        new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE)
                )
        );
        Channel testChannel2 = new Channel(
                "channelId",
                Arrays.asList(
                        new ChatMessage("Hello, World!", "senderName1", "receiverName1", new Date(), MessageType.JOIN),
                        new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE)
                )
        );

        AssertionsForInterfaceTypes
                .assertThat(testChannel1)
                .usingRecursiveComparison()
                .isEqualTo(testChannel2);
    }

    @Test
    void shouldHandleNullEmptyValues() {
        Channel channelWithNullValues = new Channel(null, null);
        Assertions.assertNull(channelWithNullValues.getName());
        Assertions.assertNull(channelWithNullValues.getMessages());

        ChatMessage messageWithNullValues = new ChatMessage(null, null, null);
        Assertions.assertNull(messageWithNullValues.getSenderName());
        Assertions.assertNull(messageWithNullValues.getDate());
    }

    @Test
    void shouldManipulateMessageList() {
        Channel testChannel = new Channel(
                "General",
                new ArrayList<>()
        );

        testChannel.getMessages().add(new ChatMessage("Hello, World!", "senderName1", MessageType.JOIN));
        Assertions.assertEquals(1, testChannel.getMessages().size());

        ChatMessage messageToRemove = new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE);
        testChannel.getMessages().add(messageToRemove);
        Assertions.assertEquals(2, testChannel.getMessages().size());
        testChannel.getMessages().remove(messageToRemove);
        Assertions.assertEquals(1, testChannel.getMessages().size());

        ChatMessage messageToUpdate = testChannel.getMessages().getFirst();
        messageToUpdate.setContent("Updated Content");
        Assertions.assertEquals("Updated Content", messageToUpdate.getContent());
    }

    @Test
    void shouldTestSetterMethods() {
        Channel testChannel = new Channel(
                "channelId",
                Arrays.asList(
                        new ChatMessage("Hello, World!", "senderName1", "receiverName1", new Date(), MessageType.JOIN),
                        new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE)
                )
        );

        testChannel.setName("NewGeneral");
        Assertions.assertEquals("NewGeneral", testChannel.getName());

    }

    @Test
    void shouldTestDateHandling() {
        Channel testChannel = new Channel(
                "channelId",
                new ArrayList<>(Arrays.asList(
                        new ChatMessage("Hello, World!", "senderName1", "receiverName1", new Date(), MessageType.JOIN),
                        new ChatMessage("How are you?", "senderName2", MessageType.MESSAGE)
                ))
        );

        Date currentDate = new Date();
        testChannel.getMessages().add(new ChatMessage("New Message", "senderName3", "receiverName3", currentDate, MessageType.JOIN));
        Assertions.assertEquals(currentDate, testChannel.getMessages().get(2).getDate());
    }

    @Test
    void shouldTestEdgeCases() {
        Channel testChannel = new Channel("General", new ArrayList<>());
        Assertions.assertEquals(0, testChannel.getMessages().size());

        List<ChatMessage> largeMessageList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeMessageList.add(new ChatMessage("Message" + i, "Sender" + i, MessageType.JOIN));
        }
        Channel largeChannel = new Channel("LargeChannel", largeMessageList);
        Assertions.assertEquals(1000, largeChannel.getMessages().size());
    }
}
