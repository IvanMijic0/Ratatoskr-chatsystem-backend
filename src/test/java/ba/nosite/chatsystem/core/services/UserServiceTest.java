package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.set_id("testUserId");
        testUser.setUsername("testUsername");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER);
        testUser.setCreatedAt(LocalTime.now());
        testUser.setUpdatedAt(LocalTime.now());
    }

    @Test
    void shouldFindUserByEmailWhenValidEmailGiven() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User foundUser = userService.findUserByEmail("test@example.com");

        assertEquals(testUser, foundUser);
    }

    @Test
    void shouldReturnNullWhenFindingUserByEmailWithInvalidEmail() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        User foundUser = userService.findUserByEmail("nonexistent@example.com");

        assertNull(foundUser);
    }

    @Test
    void shouldReturnUsersResponseWhenSavingNewUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UsersResponse usersResponse = userService.save(new User());

        assertNotNull(usersResponse);
        assertEquals(testUser.get_id(), usersResponse.get_id());
    }

    @Test
    void shouldReturnListOfUsersResponseWhenListingUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(testUser);

        when(userRepository.findAll()).thenReturn(userList);

        List<UsersResponse> usersResponseList = userService.listUserResponse();

        assertEquals(1, usersResponseList.size());
        assertEquals(testUser.get_id(), usersResponseList.get(0).get_id());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdatingUserWithInvalidUserId() {
        when(userRepository.findById("nonexistentUserId")).thenReturn(Optional.empty());

        User updatedUser = new User();

        assertThrows(UserNotFoundException.class, () -> userService.update("nonexistentUserId", updatedUser));
    }

}