package com.example.springecom.config;

import com.example.springecom.model.User;
import com.example.springecom.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin user if not exists
        if (userRepo.findByUsername("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@gmail.com");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("System Admin");
            admin.setRole("ROLE_ADMIN");
            userRepo.save(admin);
            System.out.println("Seeded admin account: admin@gmail.com / admin123");
        }

        // Seed regular User if not exists
        if (userRepo.findByUsername("user@gmail.com").isEmpty()) {
            User user = new User();
            user.setUsername("user@gmail.com");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setName("Regular User");
            user.setRole("ROLE_USER");
            userRepo.save(user);
            System.out.println("Seeded user account: user@gmail.com / user123");
        }
    }
}
