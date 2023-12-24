package ba.nosite.chatsystem.core.models.user;

public record Friend(
        String _id,
        String username,
        String fullName,
        String email,
        String avatarUrl
) {
}
