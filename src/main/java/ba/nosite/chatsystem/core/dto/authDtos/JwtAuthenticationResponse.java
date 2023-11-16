package ba.nosite.chatsystem.core.dto.authDtos;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class JwtAuthenticationResponse {
    private final Instant timestamp;
    private HttpStatus status;
    private String statusText;
    private String token;
    private String refreshToken;

    public JwtAuthenticationResponse(HttpStatus status, String message, String token, String refreshToken) {
        this.status = status;
        this.statusText = message;
        this.token = token;
        this.refreshToken = refreshToken;
        this.timestamp = Instant.now();
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

