package ba.nosite.chatsystem.rest.services;

import ba.nosite.chatsystem.rest.exceptions.custom.UserNotFoundException;
import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User insert(User user) {
        UUID userId = UUID.randomUUID();
        user.set_id(userId);

        return userRepository.save(user);
    }

    public Iterable<User> list() {
        return userRepository.findAll();
    }

    public User update(UUID userId, User updatedUser) {
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

            return userRepository.save(userToUpdate);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public void delete(UUID userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
}
