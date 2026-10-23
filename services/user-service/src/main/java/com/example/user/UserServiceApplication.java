package com.example.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the User Service Spring Boot application.
 * <p>
 * This class is responsible for bootstrapping the application context and starting
 * the embedded web server. It enables auto-configuration and component scanning
 * through the {@link SpringBootApplication} annotation.
 * </p>
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method that launches the User Service application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
