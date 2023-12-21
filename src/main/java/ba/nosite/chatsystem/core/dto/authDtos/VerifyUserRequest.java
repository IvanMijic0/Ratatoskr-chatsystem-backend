package ba.nosite.chatsystem.core.dto.authDtos;

public class VerifyUserRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
