package ba.nosite.chatsystem.core.services.authServices;

import ba.nosite.chatsystem.core.dto.authDtos.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.authDtos.LoginRequest;
import ba.nosite.chatsystem.core.dto.authDtos.RegisterRequest;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.exceptions.auth.UserAlreadyExistsException;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.EmailSenderService;
import ba.nosite.chatsystem.core.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
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
                request.getUsername(),
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

    public JwtAuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            Optional<User> myb_user = userRepository.findByEmailOrUsername(request.getEmail(), request.getUsername());

            if (myb_user.isEmpty()) {
                throw new AuthenticationException("Invalid Credentials");
            }
            User user = myb_user.get();
            String jwt = jwtService.generateTokenWithAdditionalClaims(Map.of("user_id", user.get_id()), user);

            long maxAge = jwtService.extractExpiration(jwt).getTime() - System.currentTimeMillis();

            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setMaxAge((int) (maxAge / 1000));
            jwtCookie.setSecure(false); // TODO --> For testing
            jwtCookie.setHttpOnly(true);

            response.addCookie(jwtCookie);

            return new JwtAuthenticationResponse(HttpStatus.OK, "Successfully logged in", jwt);
        } catch (Exception ex) {
            throw new AuthenticationException("Invalid Credentials", ex);
        }
    }

    public boolean verifyEmailToken(String verificationCode) {
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

    public boolean verifyUser(String authorizationHeader) {
        String authHeader = authorizationHeader.substring(7);
        Optional<User> potential_user = userRepository.findByUsername(jwtService.extractUsername(authHeader));
        if (potential_user.isPresent()) {
            User user = potential_user.get();
            return jwtService.isTokenValid(authHeader, user);
        }
        return false;
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
