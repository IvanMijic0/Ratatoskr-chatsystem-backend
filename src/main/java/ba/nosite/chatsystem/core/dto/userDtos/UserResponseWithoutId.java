package ba.nosite.chatsystem.core.dto.userDtos;

import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;

import java.time.LocalTime;

public class UserResponseWithoutId {
    private final String email;
    private final LocalTime createdAt;
    private final LocalTime updatedAt;
    private final Role role;
    private String username;

    public UserResponseWithoutId(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.role = user.getRole();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }

    public LocalTime getUpdatedAt() {
        return updatedAt;
    }

    public Role getRole() {
        return role;
    }

}
