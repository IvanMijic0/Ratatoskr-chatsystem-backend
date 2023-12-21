package ba.nosite.chatsystem.core.dto.authDtos;

public record GoogleLoginRequest(
        String email,
        String firstName,
        String lastName,
        String googleId,
        String avatarImageUrl
) {
}
