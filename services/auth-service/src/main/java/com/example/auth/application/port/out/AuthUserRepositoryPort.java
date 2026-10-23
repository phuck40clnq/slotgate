package com.example.auth.application.port.out;

import java.util.Optional;

import com.example.auth.domain.model.AuthUser;

/**
 * Repository port for accessing authentication user data.
 * <p>
 * Defines the contract for persisting and retrieving {@link AuthUser}
 * entities without exposing infrastructure details to the domain layer.
 * </p>
 */
public interface AuthUserRepositoryPort {

    /**
     * Persists the given authentication user.
     *
     * @param user the {@link AuthUser} to be saved
     */
    void save(AuthUser user);

    /**
     * Finds an authentication user by email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the user if found, or empty if not found
     */
    Optional<AuthUser> findByEmail(String email);
}
