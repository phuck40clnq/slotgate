package com.example.auth.domain.model;

import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Domain model representing an authenticated user.
 * <p>
 * This model contains authentication-related information only,
 * such as user identifier, email, and password hash.
 * It does not include profile or business-related data.
 * </p>
 */
@RequiredArgsConstructor
@Getter
public class AuthUser {

    /** Unique identifier of the user. */
    @NonNull 
    private final UUID id;

    /** Email address used as the user's login identifier. */
    @NonNull 
    private final String email;

    /** Hashed password used for authentication. */
    @NonNull 
    private final String passwordHash;
}
