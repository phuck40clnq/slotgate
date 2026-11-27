package com.example.auth.support;

import java.util.UUID;

import com.example.auth.domain.model.AuthUser;

// Simulated users; the hash is never checked against a password in these tests.
public final class AuthUserFixtures {

    private static final String PASSWORD_HASH = "$2a$10$simulatedhashsimulatedhashsimulatedhashsimulatedhash.";

    private AuthUserFixtures() {
    }

    public static AuthUser user(String id, String email) {
        return new AuthUser(UUID.fromString(id), email, PASSWORD_HASH);
    }
}
