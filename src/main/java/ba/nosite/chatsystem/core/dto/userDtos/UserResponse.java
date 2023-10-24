package ba.nosite.chatsystem.core.dto.userDtos;

import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;

import java.time.LocalTime;

public class UserResponse {
    private final String _id;
    private final String name;
    private final String email;
    private final LocalTime createdAt;
    private final LocalTime updatedAt;
    private final Role role;

    public UserResponse(User user) {
        this._id = user.get_id();
        this.name = user.getFirst_name().concat(" ").concat(user.getLast_name());
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.role = user.getRole();
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
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
