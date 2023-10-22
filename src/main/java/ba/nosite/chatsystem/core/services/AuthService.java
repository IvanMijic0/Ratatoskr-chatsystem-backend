package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.LoginRequest;
import ba.nosite.chatsystem.core.dto.RegisterRequest;
import ba.nosite.chatsystem.core.models.Role;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailSenderService emailSenderService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager, EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.emailSenderService = emailSenderService;
    }

    public void register(RegisterRequest request, String siteUrl) throws MessagingException, UnsupportedEncodingException {
        var user = new User(
                request.getFirst_name(),
                request.getLast_name(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER
        );

        String randomConfirmationCode = UUID.randomUUID().toString();
        user.setVerificationCode(randomConfirmationCode);
        user.setEnabled(false);

        userRepository.save(user);

        emailSenderService.sendVerificationEmail(user, siteUrl);
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

    public boolean verify(String verificationCode) {
        Optional<User> maybe_user = userRepository.findByVerificationCode(verificationCode);

        if (maybe_user.isEmpty()) {
            return false;
        }
        User user = maybe_user.get();

        if (user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
