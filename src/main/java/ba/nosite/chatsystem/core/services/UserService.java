package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.rest.configurations.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public User save(User newUser) {
        if (newUser.get_id() == null) {
            newUser.setCreatedAt(LocalTime.now());
        }

        newUser.setUpdatedAt(LocalTime.now());
        return userRepository.save(newUser);
    }

    public Iterable<User> list() {
        return userRepository.findAll();
    }

    public User update(String userId, User updatedUser) {
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

            return userRepository.save(userToUpdate);
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
