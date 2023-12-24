package ba.nosite.chatsystem.core.dto.userDtos;

public record UserInfo(
        String _id,
        String username,
        String fullName,
        String email,
        String avatarUrl
) {
}
