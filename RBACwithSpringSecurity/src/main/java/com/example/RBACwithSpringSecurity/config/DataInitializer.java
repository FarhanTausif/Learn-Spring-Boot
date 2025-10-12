package com.example.RBACwithSpringSecurity.config;

import com.example.RBACwithSpringSecurity.model.User;
import com.example.RBACwithSpringSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default users with string-based roles
        initializeUsers();
    }

    private void initializeUsers() {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRoles(List.of("ADMIN")); // Using string-based role

            userRepository.save(admin);
        }

        // Create manager user
        if (!userRepository.existsByUsername("manager")) {
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setEmail("manager@example.com");
            manager.setFirstName("Team");
            manager.setLastName("Manager");
            manager.setRoles(List.of("MANAGER")); // Using string-based role

            userRepository.save(manager);
        }

        // Create regular user
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFirstName("Regular");
            user.setLastName("User");
            user.setRoles(List.of("USER")); // Using string-based role

            userRepository.save(user);
        }

        // Create a user with multiple roles to demonstrate the capability
        if (!userRepository.existsByUsername("superuser")) {
            User superuser = new User();
            superuser.setUsername("superuser");
            superuser.setPassword(passwordEncoder.encode("super123"));
            superuser.setEmail("superuser@example.com");
            superuser.setFirstName("Super");
            superuser.setLastName("User");
            superuser.setRoles(List.of("ADMIN", "MANAGER", "USER")); // Multiple roles

            userRepository.save(superuser);
        }
    }
}
