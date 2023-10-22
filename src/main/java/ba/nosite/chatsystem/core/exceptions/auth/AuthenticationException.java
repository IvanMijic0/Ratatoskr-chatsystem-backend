package ba.nosite.chatsystem.core.exceptions.auth;

import ba.nosite.chatsystem.core.exceptions.general.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends GeneralException {
    public AuthenticationException() {
        super(HttpStatus.UNAUTHORIZED.value());
    }

    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }

    public AuthenticationException(String message, Exception e) {
        super(message, e);
    }
}
