package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.userDtos.UserResponseWithoutId;
import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ba.nosite.chatsystem.helpers.jwtUtils.extractJwtFromHeader;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public User findUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    public User findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    public User findUserByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> optionalUser = userRepository.findByEmailOrUsername(usernameOrEmail);
        return optionalUser.orElse(null);
    }

    public User findUserByVerificationCode(String verificationCode) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        return optionalUser.orElse(null);
    }

    public UsersResponse save(User newUser) {
        if (newUser.get_id() == null) {
            newUser.setCreatedAt(LocalTime.now());
            newUser.setRole(Role.ROLE_USER);
        }
        newUser.setUpdatedAt(LocalTime.now());

        return new UsersResponse(userRepository.save(newUser));
    }

    public List<UsersResponse> list() {
        List<User> users = userRepository.findAll();

        return users
                .stream()
                .map(UsersResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponseWithoutId update(String userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            if (updatedUser.getFirst_name() != null) {
                userToUpdate.setFirst_name(updatedUser.getFirst_name());
            }
            if (updatedUser.getLast_name() != null) {
                userToUpdate.setLast_name(updatedUser.getLast_name());
            }
            if (updatedUser.getEmail() != null) {
                userToUpdate.setEmail(updatedUser.getEmail());
            }
            updatedUser.setUpdatedAt(LocalTime.now());

            return new UserResponseWithoutId(userRepository.save(userToUpdate));
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public void deleteById(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("User not found with ID: ".concat(userId));
        }
    }

    public UserResponseWithoutId getUserById(String authHeader) {
        String jwt = extractJwtFromHeader(authHeader);
        String username = jwtService.extractUsername(jwt);

        Optional<User> potentialUser = userRepository.findByUsername(username);

        if (potentialUser.isPresent()) {
            User user = potentialUser.get();
            return new UserResponseWithoutId(user);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public boolean checkIfUserIsInDatabaseByEmail(String email) {
        Optional<User> potentialUser = userRepository.findByEmail(email);
        return potentialUser.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailOrUsername(username);

        return user.orElse(null);
    }
}
