package ba.nosite.chatsystem.helpers;

import ba.nosite.chatsystem.core.dto.ExceptionResponse.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class customJSONResponse {
    public static void jsonResponse(HttpServletResponse response, String message) throws IOException {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String statusCodeText = httpStatus.getReasonPhrase();

        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), statusCodeText, message);

        ObjectMapper objectMapper = new ObjectMapper();
        String errorResponseJson = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        response.getWriter().write(errorResponseJson);
    }
}
