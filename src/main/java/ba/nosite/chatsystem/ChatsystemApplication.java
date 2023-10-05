package ba.nosite.chatsystem;

import ba.nosite.chatsystem.rest.models.User;
import ba.nosite.chatsystem.rest.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class ChatsystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatsystemApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserService userService) {
        return args -> {
            // Read JSON and write to DB
            User user = new User(
                    "John",
                    "Doe",
                    "john@gmail.com"
            );
            userService.insert(user);
        };
    }
}
