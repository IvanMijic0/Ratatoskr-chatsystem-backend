package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.LoginRequest;
import ba.nosite.chatsystem.core.dto.RegisterRequest;
import ba.nosite.chatsystem.core.models.Role;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepository, UserService userService, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public JwtAuthenticationResponse register(RegisterRequest request) {
        var user = new User(
                request.getFirst_name(),
                request.getLast_name(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER
        );

        user = userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public ResponseEntity<JwtAuthenticationResponse> login(LoginRequest request) {

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

            var jwt = jwtService.generateToken(user);

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtAuthenticationResponse("Authentication failed"));
        }
    }
}
