package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.userDtos.UserEmail;
import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list() {
        List<UsersResponse> users = userService.list();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody User updatedUser) {
        try {
            return ResponseEntity.ok(userService.update(userId, updatedUser));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    "User with ID "
                            .concat(userId)
                            .concat(" has been successfully deleted.")
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/specific")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(userService.getUserById(authHeader));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/checkIfExists")
    public ResponseEntity<?> checkIfUserExistsInDatabaseByEmail(@RequestBody UserEmail user) {
        if (userService.checkIfUserIsInDatabaseByEmail(user.email())) {
            return ResponseEntity.ok("User exists in database.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in database.");
    }
}
