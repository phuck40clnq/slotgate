package com.example.auth.application.port.out;

public interface PasswordHasherPort {
    String hash(String rawPassword);
}
