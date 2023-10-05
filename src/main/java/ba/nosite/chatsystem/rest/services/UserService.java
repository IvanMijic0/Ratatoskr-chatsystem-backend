package ba.nosite.chatsystem.rest.services;

import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void insert(User user) {
        userRepository.insert(user);
    }

    public Iterable<User> list() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> save(List<User> users) {
        List<User> savedUsers = new ArrayList<>();
        for (User user : users) {
            savedUsers.add(userRepository.save(user));
        }
        return savedUsers;
    }
}
