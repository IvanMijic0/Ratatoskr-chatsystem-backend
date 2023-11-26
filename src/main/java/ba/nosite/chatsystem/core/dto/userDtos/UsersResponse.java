package ba.nosite.chatsystem.core.dto.userDtos;

import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;

import java.time.LocalTime;

public class UsersResponse {
    private String _id;
    private String username;
    private String email;
    private LocalTime createdAt;
    private LocalTime updatedAt;
    private Role role;

    public UsersResponse(User user) {
        if (user != null) {
            this._id = user.get_id();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.createdAt = user.getCreatedAt();

            if (user.getUpdatedAt() != null) {
                this.updatedAt = user.getUpdatedAt();
            } else {
                this.updatedAt = LocalTime.now();
            }

            this.role = user.getRole();
        }
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
