package ba.nosite.chatsystem.core.dto.ExceptionResponse;

public class ErrorResponse {
    private int statusCode;
    private String statusText;
    private String message;

    public ErrorResponse(int statusCode, String statusText, String message) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
