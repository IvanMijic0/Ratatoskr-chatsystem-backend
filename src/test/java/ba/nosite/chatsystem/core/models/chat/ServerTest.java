package ba.nosite.chatsystem.core.models.chat;

import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

    @Test
    void shouldCreateServer() {
        String serverName = "MyServer";
        String ownerId = "owner123";
        List<User> members = new ArrayList<>();
        String serverId = "server123";
        List<Channel> channels = new ArrayList<>();

        Server server = new Server(serverName, ownerId, members, serverId, channels);

        assertEquals(serverName, server.getName());
        assertEquals(ownerId, server.getOwnerId());
        assertEquals(members, server.getMembers());
        assertEquals(serverId, server.getId());
        assertEquals(channels, server.getChannels());
    }

    @Test
    void shouldSetAndGetChannels() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), "server123", new ArrayList<>());

        List<Channel> newChannels = new ArrayList<>();
        newChannels.add(new Channel("channel123", "General", "server123", new ArrayList<>()));
        server.setChannels(newChannels);

        assertEquals(newChannels, server.getChannels());
    }

    @Test
    void shouldSetAndGetId() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), "server123", new ArrayList<>());

        String newId = "newServerId";
        server.setId(newId);

        assertEquals(newId, server.getId());
    }

    @Test
    void shouldSetAndGetName() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), "server123", new ArrayList<>());

        String newName = "NewServerName";
        server.setName(newName);

        assertEquals(newName, server.getName());
    }

    @Test
    void shouldSetAndGetOwnerId() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), "server123", new ArrayList<>());

        String newOwnerId = "newOwner123";
        server.setOwnerId(newOwnerId);

        assertEquals(newOwnerId, server.getOwnerId());
    }

    @Test
    void shouldSetAndGetMembers() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), "server123", new ArrayList<>());

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
