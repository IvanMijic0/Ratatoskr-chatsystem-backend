package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Iterable<User> list() {
        return userService.list();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        UUID userId = UUID.randomUUID();
        user.set_id(userId);

        User createdUser = userService.insert(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
