package ba.nosite.chatsystem.core.dto.authDtos;

public record GoogleRegisterRequest(
        String email,
        String firstName,
        String lastName,
        String password,
        String googleId,
        String avatarImageUrl
) {
}
