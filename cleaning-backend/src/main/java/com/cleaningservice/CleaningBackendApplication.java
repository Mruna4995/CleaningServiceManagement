package com.cleaningservice;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CleaningBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CleaningBackendApplication.class, args);
	}
	@Bean
    public CommandLineRunner generateAdminPassword() {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hash = encoder.encode("admin123");
            System.out.println("Generated ADMIN password hash: " + hash);
        };
    }
}

