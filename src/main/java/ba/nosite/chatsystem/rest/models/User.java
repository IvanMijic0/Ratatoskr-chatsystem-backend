package ba.nosite.chatsystem.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
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
    private String email;

    public User(String first_name, String last_name, String email) {
        this._id = UUID.randomUUID();
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
    }
}
