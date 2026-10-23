package com.example.auth.infrastructure.security.password;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.auth.application.port.out.PasswordHasherPort;
import com.example.auth.application.port.out.PasswordVerifierPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BCryptPasswordVerifier
        implements PasswordHasherPort, PasswordVerifierPort {
    
    private final PasswordEncoder encoder;

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return this.encoder.matches(rawPassword, passwordHash);
    }

    @Override
    public String hash(String rawPassword) {
        return this.encoder.encode(rawPassword);
    }

}
