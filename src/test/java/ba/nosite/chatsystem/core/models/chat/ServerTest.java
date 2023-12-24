package ba.nosite.chatsystem.core.models.chat;

import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    @Test
    void shouldCreateServer() {
        String serverName = "MyServer";
        String ownerId = "owner123";
        List<User> members = new ArrayList<>();
        String serverId = "server123";
        List<ChannelCluster> channels = new ArrayList<>();

        Server server = new Server(serverName, ownerId, members, channels, "", new Date(System.currentTimeMillis()));

        assertEquals(serverName, server.getName());
        assertEquals(ownerId, server.getOwnerId());
        assertEquals(members, server.getMembers());
        assertEquals(serverId, server.get_id());
        assertEquals(channels, server.getChannelClusters());
    }

    @Test
    void shouldSetAndGetChannels() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "", new Date(System.currentTimeMillis()));

        List<ChannelCluster> newChannels = new ArrayList<>();
        newChannels.add(new ChannelCluster("General", new ArrayList<>()));
        server.setChannelClusters(newChannels);

        assertEquals(newChannels, server.getChannelClusters());
    }

    @Test
    void shouldSetAndGetId() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "", new Date(System.currentTimeMillis()));

        String newId = "newServerId";
        server.set_id(newId);

        assertEquals(newId, server.get_id());
    }

    @Test
    void shouldSetAndGetName() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "", new Date(System.currentTimeMillis()));

        String newName = "NewServerName";
        server.setName(newName);

        assertEquals(newName, server.getName());
    }

    @Test
    void shouldSetAndGetOwnerId() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "", new Date(System.currentTimeMillis()));

        String newOwnerId = "newOwner123";
        server.setOwnerId(newOwnerId);

        assertEquals(newOwnerId, server.getOwnerId());
    }

    @Test
    void shouldSetAndGetMembers() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "", new Date(System.currentTimeMillis()));

        List<User> newMembers = new ArrayList<>();
        newMembers.add(new User(
                "Alice",
                "Baker",
                "alice#12",
                "alica@gmail.com",
                "p1234",
                Role.ROLE_USER)
        );
        server.setMembers(newMembers);

        assertEquals(newMembers, server.getMembers());
    }
}
