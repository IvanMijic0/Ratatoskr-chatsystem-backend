package ba.nosite.chatsystem.helpers;

import ba.nosite.chatsystem.core.dto.authDtos.LoginRequest;
import ba.nosite.chatsystem.core.exceptions.auth.AuthenticationException;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;

import java.util.Map;
import java.util.Optional;

public class JwtUtils {
    public static String generateJwt(LoginRequest request, UserRepository userRepository, JwtService jwtService) {
        Optional<User> myb_user = userRepository.findByEmailOrUsername(request.getUsernameOrEmail());

        if (myb_user.isEmpty()) {
            throw new AuthenticationException("Invalid Credentials");
        }
        User user = myb_user.get();
        return jwtService.generateTokenWithAdditionalClaims(Map.of("user_id", user.get_id()), user);
    }
}
