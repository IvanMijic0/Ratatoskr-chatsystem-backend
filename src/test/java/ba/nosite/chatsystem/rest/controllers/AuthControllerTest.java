package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.authDtos.GoogleLoginRequest;
import ba.nosite.chatsystem.core.dto.authDtos.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.authDtos.LoginRequest;
import ba.nosite.chatsystem.core.dto.authDtos.RegisterRequest;
import ba.nosite.chatsystem.core.exceptions.auth.RegistrationException;
import ba.nosite.chatsystem.core.services.JsonService;
import ba.nosite.chatsystem.core.services.authServices.AuthService;
import ba.nosite.chatsystem.rest.configurations.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private JsonService jsonService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonService.toJson(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldHandleRegistrationException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        doThrow(new RegistrationException("Failed to Register User."))
                .when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonService.toJson(registerRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("token");
        when(authService.login(any(LoginRequest.class))).thenReturn(jwtAuthenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonService.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldLoginWithGoogleSuccessfully() throws Exception {
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(
                "test@example.com",
                "John",
                "Doe",
                "googleId123",
                "https://example.com/avatar.jpg"
        );

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse("token");
        when(authService.loginWithGoogle(any(GoogleLoginRequest.class))).thenReturn(jwtAuthenticationResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/loginWithGoogle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonService.toJson(googleLoginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldVerifyEmailTokenSuccessfully() throws Exception {
        String verificationCode = "validCode";
        when(authService.verifyEmailToken(verificationCode)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auth/verifyEmailToken")
                        .param("code", verificationCode))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldValidateTokenSuccessfullyWithAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/validateToken")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
