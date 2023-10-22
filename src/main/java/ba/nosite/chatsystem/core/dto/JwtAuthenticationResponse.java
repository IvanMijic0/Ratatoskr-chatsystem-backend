package ba.nosite.chatsystem.core.dto;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class JwtAuthenticationResponse {
    static String token;

    public static ResponseEntity<Object> generateResponse(String tkn, HttpStatusCode status) {
        token = tkn;

        return new ResponseEntity<>(token, status);
    }
}
