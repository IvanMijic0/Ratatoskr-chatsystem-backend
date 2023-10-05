package ba.nosite.chatsystem.rest.services;

import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User insert(User user) {
        userRepository.insert(user);
        return user;
    }

    public Iterable<User> list() {
        return userRepository.findAll();
    }
}
