package ba.nosite.chatsystem.core.services.authServices;

import ba.nosite.chatsystem.core.dto.authDtos.*;
import ba.nosite.chatsystem.core.dto.userDtos.UserResponseWithoutId;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.exceptions.auth.UserAlreadyExistsException;
import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.EmailSenderService;
import ba.nosite.chatsystem.core.services.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import static ba.nosite.chatsystem.core.services.authServices.JwtService.extractJwtFromHeader;

@Service
public class AuthService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    @Value("${website.frontend.url}")
    private String frontendUrl;

    public AuthService(BCryptPasswordEncoder passwordEncoder, JwtService jwtService, EmailSenderService emailSenderService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
    }

    public void register(RegisterRequest request) throws
            MessagingException,
            UnsupportedEncodingException,
            UserAlreadyExistsException {
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER
        );
        User existingUser = userService.findUserByEmail(user.getEmail());
        if (existingUser != null) {
            if (existingUser.getEnabled().equals(true)) {
                throw new UserAlreadyExistsException("User already exists in database.");
            }
            userService.deleteById(existingUser.get_id());
        }
        user.setVerificationCode(jwtService.generateToken(user));
        user.setEnabled(false);

        userService.save(user);
        emailSenderService.sendVerificationEmail(user, frontendUrl);
    }

    public void registerWithGoogle(GoogleRegisterRequest request) throws UserAlreadyExistsException {
        User user = new User(
                request.firstName()
                        .concat(request.lastName())
                        .concat("#")
                        .concat(UUID.randomUUID().toString().substring(0, 8)),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.ROLE_USER
        );
        user.setGoogleId(request.googleId());
        user.setAvatarImageUrl(request.avatarImageUrl());
        user.setFirst_name(request.firstName());
        user.setLast_name(request.lastName());

        user.setEnabled(true);
        userService.save(user);

        new UserResponseWithoutId(user);
    }

    public JwtAuthenticationResponse login(LoginRequest request) {
        try {
            User user = userService.findUserByUsernameOrEmail(request.getUsernameOrEmail());

            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthenticationException("Invalid Credentials");
            }
            return getJwtAuthenticationResponse(user);
        } catch (Exception ex) {
            throw new AuthenticationException("Invalid Credentials", ex);
        }
    }

    public JwtAuthenticationResponse loginWithGoogle(GoogleLoginRequest request) {
        try {
            User user = userService.findUserByEmail(request.email());
            userService.save(user);

            return getJwtAuthenticationResponse(user);
        } catch (Exception ex) {
            throw new AuthenticationException("Invalid Credentials", ex);
        }
    }

    public boolean verifyEmailToken(String verificationCode) {
        User user = userService.findUserByVerificationCode(verificationCode);
        if (user == null) {
            return false;
        }
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

    public boolean validateToken(String authHeader) {
        String jwt = extractJwtFromHeader(authHeader);
        User user = userService.findUserByUsername(jwtService.extractUsername(jwt));
        if (user == null) {
            return false;
        }
        return jwtService.isTokenValid(jwt, user);
    }

    public JwtAuthenticationResponse refreshToken(String jwtRefresh) {
        if (jwtRefresh != null) {
            User user = userService.findUserByUsername(jwtService.extractUsername(jwtRefresh));
            if (user != null) {
                return getJwtAuthenticationResponse(user);
            }
        }
        throw new AuthenticationException("Invalid Credentials");
    }

    private JwtAuthenticationResponse getJwtAuthenticationResponse(User user) {
        String jwt = jwtService.generateTokenWithAdditionalClaims(Map.of("user_id", user.get_id()), user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        return new JwtAuthenticationResponse(HttpStatus.OK, "Successfully logged in", jwt, jwtRefreshToken);
    }
}
