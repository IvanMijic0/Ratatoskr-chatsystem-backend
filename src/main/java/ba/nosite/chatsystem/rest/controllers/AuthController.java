package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.authDtos.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.authDtos.LoginRequest;
import ba.nosite.chatsystem.core.dto.authDtos.RegisterRequest;
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
@RequestMapping("/api/v1/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest user, HttpServletRequest request) {
        try {
            authService.register(user, authService.getSiteURL(request));
            return ResponseEntity.ok("Successfully sent Verification Code!");
        } catch (Exception e) {
            throw new RegistrationException("Failed to Register User.", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) throws LoginException {
        try {
            return ResponseEntity.ok(authService.login(request, response));
        } catch (Exception e) {
            throw new LoginException();
        }
    }

    @GetMapping("/verifyEmailToken")
    public ResponseEntity<?> verifyEmailToken(@Param("code") String code) {
        if (authService.verifyEmailToken(code)) {
            return ResponseEntity.ok("Successful verification!");
        }
        return ResponseEntity.status(403).body("Confirmation token expired!");
    }

    @PostMapping("/validateToken")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> verifyUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authService.verifyUser(authorizationHeader)) {
            return ResponseEntity.ok("Successful verification!");
        }
        return ResponseEntity.status(403).body("Confirmation token expired!");
    }
}