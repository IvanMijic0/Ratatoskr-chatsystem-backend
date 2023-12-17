package ba.nosite.chatsystem.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    public <T> T deserializeJsonValue(String jsonValue, Class<T> clazz) {
        try {
            return fromJson(jsonValue, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON value", e);
        }
    }
}
