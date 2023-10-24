package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.LoginRequest;
import ba.nosite.chatsystem.core.dto.RegisterRequest;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.exceptions.auth.UserAlreadyExistsException;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;
import ba.nosite.chatsystem.core.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailSenderService emailSenderService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager, EmailSenderService emailSenderService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
    }

    public void register(RegisterRequest request, String siteUrl) throws
            MessagingException,
            UnsupportedEncodingException,
            UserAlreadyExistsException {
        User user = new User(
                request.getFirst_name(),
                request.getLast_name(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER
        );
        Optional<User> potentialExistingUser = userRepository.findByEmail(user.getEmail());
        if (potentialExistingUser.isPresent()) {
            User existingUser = potentialExistingUser.get();
            if (existingUser.getEnabled().equals(true)) {
                throw new UserAlreadyExistsException("User already exists in database.");
            }
            userService.delete(existingUser.get_id());
        }
        user.setVerificationCode(jwtService.generateToken(user));
        user.setEnabled(false);

        userService.save(user);
        emailSenderService.sendVerificationEmail(user, siteUrl);
    }

    public ResponseEntity<JwtAuthenticationResponse> login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

            String jwt = jwtService.generateToken(user);

            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
        } catch (Exception ex) {
            throw new AuthenticationException("Invalid Credentials");
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
        }

        if (jwtService.isTokenValid(user.getVerificationCode(), user)) {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userService.save(user);
            return true;
        }
        return false;
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
