package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.LoginRequest;
import ba.nosite.chatsystem.core.dto.RegisterRequest;
import ba.nosite.chatsystem.core.exceptions.auth.RegistrationException;
import ba.nosite.chatsystem.core.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest user, HttpServletRequest request) {
        try {
            authService.register(user, authService.getSiteURL(request));
            return "Verification code sent";
        } catch (Exception e) {
            throw new RegistrationException("Failed to register user.", e);
        }
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (authService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
}