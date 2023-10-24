package ba.nosite.chatsystem.rest.configurations;

import ba.nosite.chatsystem.core.models.User;
import ba.nosite.chatsystem.core.models.enums.Role;
import ba.nosite.chatsystem.core.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SeedDataConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public SeedDataConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            admin.setEnabled(true);
            admin.setCreatedAt(LocalTime.now());
            admin.setUpdatedAt(LocalTime.now());

            userRepository.save(admin);
            System.out.println("created ADMIN user - ".concat(admin.toString()));
        }
    }
}
