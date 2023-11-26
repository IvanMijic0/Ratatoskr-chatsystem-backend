package ba.nosite.chatsystem.core.repository;

import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        User user = new User("John", "Doe", "john_doe", "john@example.com", "password123", Role.ROLE_USER);
        userRepository.save(user);

        Optional<User> savedUser = userRepository.findById(user.get_id());
        Assertions.assertTrue(savedUser.isPresent());
        Assertions.assertEquals("John", savedUser.get().getFirst_name());
    }

    @Test
    public void shouldDeleteUser() {
        User user = new User("Jane", "Doe", "jane_doe", "jane@example.com", "password456", Role.ROLE_USER);
        userRepository.save(user);

        userRepository.deleteById(user.get_id());

        Optional<User> deletedUser = userRepository.findById(user.get_id());
        Assertions.assertFalse(deletedUser.isPresent());
    }

    @Test
    public void shouldUpdateUser() {
        User user = new User("Bob", "Smith", "bob_smith", "bob@example.com", "oldpassword", Role.ROLE_USER);
        userRepository.save(user);

        user.setEmail("bob_updated@example.com");
        userRepository.save(user);

        Optional<User> updatedUser = userRepository.findById(user.get_id());
        Assertions.assertTrue(updatedUser.isPresent());
        Assertions.assertEquals("bob_updated@example.com", updatedUser.get().getEmail());
    }

    @Test
    public void shouldFindByEmailOrUsername() {
        User user = new User("testUser", "test@example.com", "password789", Role.ROLE_USER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmailOrUsername("test@example.com");
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    public void shouldFindByEmail() {
        User user = new User("testUser2", "test2@example.com", "password123", Role.ROLE_USER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test2@example.com");
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("testUser2", foundUser.get().getUsername());
    }

    @Test
    public void shouldFindByUsername() {
        User user = new User("testUser3", "test3@example.com", "password456", Role.ROLE_USER);
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testUser3");
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("test3@example.com", foundUser.get().getEmail());
    }

    @Test
    public void shouldFindByVerificationCode() {
        User user = new User("testUser4", "test4@example.com", "password789", Role.ROLE_USER);
        user.setVerificationCode("verification123");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByVerificationCode("verification123");
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals("test4@example.com", foundUser.get().getEmail());
    }
}
