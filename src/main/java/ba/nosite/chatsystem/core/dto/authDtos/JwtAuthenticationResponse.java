package ba.nosite.chatsystem.core.dto.authDtos;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class JwtAuthenticationResponse {
    private final Instant timestamp;
    private HttpStatus status;
    private String statusText;
    private String token;

    public JwtAuthenticationResponse(HttpStatus status, String message, String token) {
        this.status = status;
        this.statusText = message;
        this.token = token;
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

