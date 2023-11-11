package ba.nosite.chatsystem.core.services.authServices;

import ba.nosite.chatsystem.core.dto.authDtos.JwtAuthenticationResponse;
import ba.nosite.chatsystem.core.dto.authDtos.LoginRequest;
import ba.nosite.chatsystem.core.dto.authDtos.RegisterRequest;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.exceptions.auth.UserAlreadyExistsException;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;
import ba.nosite.chatsystem.core.services.EmailSenderService;
import ba.nosite.chatsystem.core.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static ba.nosite.chatsystem.helpers.CookieUtils.extractCookieFromJwt;
import static ba.nosite.chatsystem.helpers.CookieUtils.setJwtCookie;

@Service
public class AuthService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    @Value("${website.frontend.url}")
    private String frontendUrl;

    public AuthService(BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager, EmailSenderService emailSenderService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
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
            userService.delete(existingUser.get_id());
        }
        user.setVerificationCode(jwtService.generateToken(user));
        user.setEnabled(false);

        userService.save(user);
        emailSenderService.sendVerificationEmail(user, frontendUrl);
    }

    public JwtAuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userService.findUserByUsernameOrEmail(request.getEmail(), request.getUsername());

            if (user == null) {
                throw new AuthenticationException("Invalid Credentials");
            }
            // Could be useful, I do not know what more I would need in my payload
            String jwt = jwtService.generateTokenWithAdditionalClaims(Map.of("user_id", user.get_id()), user);
            String jwtRefreshToken = jwtService.generateRefreshToken(user);

            setJwtCookie(response, jwt, jwtService);
            setJwtCookie(response, jwtRefreshToken, "jwtRefresh", jwtService);

            return new JwtAuthenticationResponse(HttpStatus.OK, "Successfully logged in", jwt);
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

    public boolean verifyUser(HttpServletRequest request) {
        String jwt = extractCookieFromJwt(request, "jwt");
        if (jwt != null) {
            User user = userService.findUserByUsername(jwtService.extractUsername(jwt));
            if (user == null) {
                return false;
            }
            return jwtService.isTokenValid(jwt, user);
        }
        return false;
    }

    public boolean refreshJwtCookie(HttpServletRequest request, HttpServletResponse response) {
        String jwtRefresh = extractCookieFromJwt(request, "jwtRefresh");
        if (jwtRefresh == null) {
            return false;
        }
        User user = userService.findUserByUsername(jwtService.extractUsername(jwtRefresh));

        if (user != null) {
            String jwt = jwtService.generateTokenWithAdditionalClaims(Map.of("user_id", user.get_id()), user);
            setJwtCookie(response, jwt, jwtService);
            return true;
        }
        return false;
    }
}
