package ba.nosite.chatsystem.core.exceptions.auth;


import ba.nosite.chatsystem.core.exceptions.general.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RegistrationException extends GeneralException {
    public RegistrationException() {
        super(HttpStatus.BAD_REQUEST.value());
    }

    public RegistrationException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

    public RegistrationException(String message, Exception e) {
        super(message, e);
    }
}