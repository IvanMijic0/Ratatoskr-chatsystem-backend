package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.rest.exceptions.custom.UserNotFoundException;
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
    public ResponseEntity<?> list() {
        try {
            Iterable<User> users = userService.list();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the user list: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.insert(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody User updatedUser) {
        try {
            User updated = userService.update(userId, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.status(HttpStatus.OK).body("User with ID " + userId + " has been successfully deleted.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
