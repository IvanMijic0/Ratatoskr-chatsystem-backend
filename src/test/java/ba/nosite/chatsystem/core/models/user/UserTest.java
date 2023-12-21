package ba.nosite.chatsystem.core.models.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @Test
    void shouldCreateNewUser() {
        User testUser = new User(
                "Tester",
                "Testić",
                "tester#12",
                "tester@gmail.com",
                "password1234",
                Role.ROLE_USER
        );

        assertEquals("Tester Testić", testUser.getFull_name());
        assertEquals("tester#12", testUser.getUsername());
        assertEquals("tester@gmail.com", testUser.getEmail());
        assertEquals("password1234", testUser.getPassword());
        assertEquals(Role.ROLE_USER, testUser.getRole());
    }

    @Test
    void shouldCompareTwoUsers() {
        User testUser1 = new User(
                "Tester",
                "Testić",
                "tester#12",
                "tester@gmail.com",
                "password1234",
                Role.ROLE_USER
        );
        User testUser2 = new User(
                "Tester",
                "Testić",
                "tester#12",
                "tester@gmail.com",
                "password1234",
                Role.ROLE_USER
        );

        assertEquals(testUser1, testUser2);
        assertEquals(testUser1.hashCode(), testUser2.hashCode());
    }

    @Test
    void shouldNotThrowExceptionForValidEmail() {
        Assertions.assertDoesNotThrow(() -> new User(
                "John",
                "Doe",
                "john_doe",
                "john.doe@example.com",
                "password123",
                Role.ROLE_USER
        ));
    }

    // TODO Ask professor about backend validation

    @Test
    void shouldGenerateFullName() {
        User user = new User(
                "John",
                "Doe",
                "john_doe",
                "john.doe@example.com",
                "password123",
                Role.ROLE_USER
        );

        assertEquals("John Doe", user.getFull_name());
    }
}
