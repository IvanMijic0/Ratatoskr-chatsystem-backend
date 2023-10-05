package ba.nosite.chatsystem.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "user")
public class User {
    @Id
    private UUID _id;
    private String first_name;
    private String last_name;
    @Indexed(unique = true)
    private String email;

    public User() {
    }
}
