package ba.nosite.chatsystem.rest.configurations;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
