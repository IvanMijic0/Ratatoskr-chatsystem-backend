package ba.nosite.chatsystem.rest.services;

import ba.nosite.chatsystem.rest.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.rest.dto.LoginRequest;
import ba.nosite.chatsystem.rest.dto.RegisterRequest;
import ba.nosite.chatsystem.rest.models.Role;
import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public JwtAuthenticationResponse register(RegisterRequest request) {
        var user = User
                .builder()
                .first_name(request.getFirst_name())
                .last_name(request.getLast_name())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        user = userService.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    public ResponseEntity<JwtAuthenticationResponse> login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
            var jwt = jwtService.generateToken(user);

            return ResponseEntity.ok(JwtAuthenticationResponse.builder().token(jwt).build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtAuthenticationResponse("Authentication failed"));
        }
    }
}
