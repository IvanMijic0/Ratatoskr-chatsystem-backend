package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ServerRepositoryTest {

    @Autowired
    private ServerRepository serverRepository;

    @Test
    public void shouldSaveServer() {
        Server server = new Server("MyServer", "owner123", new ArrayList<>(), new ArrayList<>(), "");
        serverRepository.save(server);

        Optional<Server> savedServer = serverRepository.findById(server.get_id());
        Assertions.assertTrue(savedServer.isPresent());
        Assertions.assertEquals("MyServer", savedServer.get().getName());
    }

    @Test
    public void shouldDeleteServer() {
        Server server = new Server("ServerToDelete", "owner123", new ArrayList<>(), new ArrayList<>(), "");
        serverRepository.save(server);

        serverRepository.deleteById(server.get_id());

        Optional<Server> deletedServer = serverRepository.findById(server.get_id());
        Assertions.assertFalse(deletedServer.isPresent());
    }

    @Test
    public void shouldUpdateServer() {
        Server server = new Server("ServerToUpdate", "owner123", new ArrayList<>(), new ArrayList<>(), "");
        serverRepository.save(server);

        server.setName("UpdatedServer");
        serverRepository.save(server);

        Optional<Server> updatedServer = serverRepository.findById(server.get_id());
        Assertions.assertTrue(updatedServer.isPresent());
        Assertions.assertEquals("UpdatedServer", updatedServer.get().getName());
    }

    @Test
    public void shouldFindAllServers() {
        List<Server> servers = serverRepository.findAll();
        Assertions.assertTrue(servers.isEmpty());
    }

    @Test
    public void shouldFindServerById() {
        Server server = new Server("ServerById", "owner123", new ArrayList<>(), new ArrayList<>(), "");
        serverRepository.save(server);

        Optional<Server> foundServer = serverRepository.findById(server.get_id());
        Assertions.assertTrue(foundServer.isPresent());
        Assertions.assertEquals("ServerById", foundServer.get().getName());
    }

}
