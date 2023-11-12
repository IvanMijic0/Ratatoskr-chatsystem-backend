package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.authDtos.*;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.exceptions.auth.RegistrationException;
import ba.nosite.chatsystem.core.services.authServices.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest user) {
        try {
            authService.register(user);
            return ResponseEntity.ok("Successfully sent Verification Code!");
        } catch (Exception e) {
            throw new RegistrationException("Failed to Register User.", e);
        }
    }

    @PostMapping("/registerWithGoogle")
    public ResponseEntity<?> registerWithGoogle(@RequestBody GoogleRegisterRequest user) {
        try {
            authService.registerWithGoogle(user);
            return ResponseEntity.ok("Successfully registered user!");
        } catch (Exception e) {
            throw new RegistrationException("Failed to Register User.", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) throws LoginException {
        try {
            return ResponseEntity.ok(authService.login(request, response));
        } catch (Exception e) {
            throw new LoginException("Failed to Login User.");
        }
    }

    @PostMapping("/loginWithGoogle")
    public JwtAuthenticationResponse loginWithGoogle(@RequestBody GoogleLoginRequest request, HttpServletResponse response) {
        try {
            return authService.loginWithGoogle(request, response);
        } catch (AuthenticationException ex) {
            throw new AuthenticationException("Invalid Credentials", ex);
        }
    }

    @GetMapping("/verifyEmailToken")
    public ResponseEntity<?> verifyEmailToken(@Param("code") String code) {
        if (authService.verifyEmailToken(code)) {
            return ResponseEntity.ok("Successful verification!");
        }
        return ResponseEntity.status(401).body("Confirmation token expired!");
    }

    @PostMapping("/validateToken")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> verifyUser(HttpServletRequest request) {
        if (authService.verifyUser(request)) {
            return ResponseEntity.ok("Successful validation!");
        }
        return ResponseEntity.status(401).body("Confirmation token expired or not present!");
    }

    @GetMapping("/refreshToken")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        if (authService.refreshJwtCookie(request, response)) {
            return ResponseEntity.ok("Successfully Refreshed token");
        }
        return ResponseEntity.status(401).body("Failed to refresh token");
    }
}