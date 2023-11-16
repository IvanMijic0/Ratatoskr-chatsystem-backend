package ba.nosite.chatsystem.core.dto.userDtos;

import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;

import java.time.LocalTime;

public class UsersResponse {
    private final String _id;
    private final String username;
    private final String email;
    private final LocalTime createdAt;
    private final LocalTime updatedAt;
    private final Role role;

    public UsersResponse(User user) {
        this._id = user.get_id();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.role = user.getRole();
    }

    public String getUsername() {
        return username;
    }

    public String get_id() {
        return _id;
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
