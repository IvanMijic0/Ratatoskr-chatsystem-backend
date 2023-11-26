package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.chat.WebRTCPeer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class WebRTCPeerRepositoryTest {

    @Autowired
    private WebRTCPeerRepository webRTCPeerRepository;

    @Test
    public void shouldSaveWebRTCPeer() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("username123", "session123");
        webRTCPeerRepository.save(webRTCPeer);

        Optional<WebRTCPeer> savedWebRTCPeer = webRTCPeerRepository.findById(webRTCPeer.getId());
        Assertions.assertTrue(savedWebRTCPeer.isPresent());
        Assertions.assertEquals("username123", savedWebRTCPeer.get().getUsername());
    }

    @Test
    public void shouldDeleteWebRTCPeer() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("userToDelete", "sessionToDelete");
        webRTCPeerRepository.save(webRTCPeer);

        webRTCPeerRepository.deleteById(webRTCPeer.getId());

        Optional<WebRTCPeer> deletedWebRTCPeer = webRTCPeerRepository.findById(webRTCPeer.getId());
        Assertions.assertFalse(deletedWebRTCPeer.isPresent());
    }

    @Test
    public void shouldUpdateWebRTCPeer() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("userToUpdate", "sessionToUpdate");
        webRTCPeerRepository.save(webRTCPeer);

        webRTCPeer.setUsername("updatedUser");
        webRTCPeerRepository.save(webRTCPeer);

        Optional<WebRTCPeer> updatedWebRTCPeer = webRTCPeerRepository.findById(webRTCPeer.getId());
        Assertions.assertTrue(updatedWebRTCPeer.isPresent());
        Assertions.assertEquals("updatedUser", updatedWebRTCPeer.get().getUsername());
    }

    @Test
    public void shouldFindAllWebRTCPeers() {
        Iterable<WebRTCPeer> webRTCPeers = webRTCPeerRepository.findAll();
        Assertions.assertTrue(webRTCPeers.iterator().hasNext());
    }

    @Test
    public void shouldFindWebRTCPeerById() {
        WebRTCPeer webRTCPeer = new WebRTCPeer("userById", "sessionById");
        webRTCPeerRepository.save(webRTCPeer);

        Optional<WebRTCPeer> foundWebRTCPeer = webRTCPeerRepository.findById(webRTCPeer.getId());
        Assertions.assertTrue(foundWebRTCPeer.isPresent());
        Assertions.assertEquals("userById", foundWebRTCPeer.get().getUsername());
    }
}
