package ba.nosite.chatsystem.rest.configurations;

import ba.nosite.chatsystem.core.models.Role;
import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedDataConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public SeedDataConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User(
                    "admin",
                    "admin",
                    "admin@admin.com",
                    passwordEncoder.encode("password"),
                    Role.ROLE_ADMIN
            );

            userService.save(admin);
            System.out.println("created ADMIN user - ".concat(admin.toString()));
        }
    }
}
