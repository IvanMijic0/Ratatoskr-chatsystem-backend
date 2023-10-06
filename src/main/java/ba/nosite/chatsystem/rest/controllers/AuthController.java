package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.rest.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.rest.dto.LoginRequest;
import ba.nosite.chatsystem.rest.dto.RegisterRequest;
import ba.nosite.chatsystem.rest.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public JwtAuthenticationResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody LoginRequest request) {
        return authService.login(request).getBody();
    }
}