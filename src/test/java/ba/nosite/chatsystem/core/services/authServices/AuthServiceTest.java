package ba.nosite.chatsystem.core.services.authServices;

import ba.nosite.chatsystem.core.dto.authDtos.RegisterRequest;
import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserAlreadyExistsException;
import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.EmailSenderService;
import ba.nosite.chatsystem.core.services.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.io.UnsupportedEncodingException;

@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class AuthServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Value("${website.frontend.url}")
    private String frontendUrl;

    @Test
    public void shouldRegisterUser() throws MessagingException, UnsupportedEncodingException, UserAlreadyExistsException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("testPassword");

        User user = new User(
                "testUser",
                "test@example.com",
                "encodedPassword",
                Role.ROLE_USER
        );
        user.setVerificationCode("verificationCode");

        Mockito.when(userService.findUserByEmail("test@example.com")).thenReturn(null);
        Mockito.when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        Mockito.when(jwtService.generateToken(user)).thenReturn("verificationCode");

        Mockito.when(userService.save(Mockito.any(User.class))).thenReturn(new UsersResponse(user));

        Mockito.doNothing().when(emailSenderService).sendVerificationEmail(user, frontendUrl);

        authService.register(registerRequest);

        Mockito.verify(userService, Mockito.times(1)).save(Mockito.any(User.class));

        Mockito.verify(emailSenderService, Mockito.times(1))
                .sendVerificationEmail(user, frontendUrl);
    }


//    @Test
//    public void shouldThrowExceptionWhenUserAlreadyExists() throws MessagingException, UnsupportedEncodingException {
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setUsername("testUser");
//        registerRequest.setEmail("test@example.com");
//        registerRequest.setPassword("testPassword");
//
//        User existingUser = new User("testUser", "test@example.com", "encodedPassword", Role.ROLE_USER);
//        existingUser.setVerificationCode("verificationCode");
//
//        Mockito.when(userService.findUserByEmail("test@example.com")).thenReturn(existingUser);
//
//        Assertions.assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
//        Mockito.verify(userService, Mockito.never()).save(Mockito.any(User.class));
//        Mockito.verify(emailSenderService, Mockito.never()).sendVerificationEmail(Mockito.any(User.class), Mockito.anyString());
//    }
}
