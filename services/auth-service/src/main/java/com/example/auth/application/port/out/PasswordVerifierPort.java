package com.example.auth.application.port.out;

public interface PasswordVerifierPort {
    boolean matches(String rawPassword, String passwordHash);
}
