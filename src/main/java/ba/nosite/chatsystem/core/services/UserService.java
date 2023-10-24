package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.UserResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;
import ba.nosite.chatsystem.core.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public UserResponse save(User newUser) {
        if (newUser.get_id() == null) {
            newUser.setCreatedAt(LocalTime.now());
            newUser.setRole(Role.ROLE_USER);
        }
        newUser.setUpdatedAt(LocalTime.now());

        return new UserResponse(userRepository.save(newUser));
    }

    public List<UserResponse> list() {
        List<User> users = userRepository.findAll();

        return users
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse update(String userId, User updatedUser) {
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

            return new UserResponse(userRepository.save(userToUpdate));
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public void delete(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
}
