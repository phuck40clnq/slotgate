package com.example.user.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.example.user.domain.model.UserProfile;

/**
 * Port interface for user persistence operations.
 * <p>
 * This interface defines the contract for saving and retrieving {@link UserProfile} entities.
 * It allows the domain layer to remain decoupled from the specific persistence implementation
 * (e.g., database, in-memory storage, or external service).
 * </p>
 */
public interface UserRepositoryPort {

    /**
     * Saves the given {@link UserProfile} entity.
     * <p>
     * The implementation may perform an insert or update depending on whether the user
     * already exists in the persistence store.
     * </p>
     *
     * @param user the user entity to save
     */
    void save(UserProfile user);

    /**
     * Finds a {@link UserProfile} entity by its id.
     * <p>
     * Returns an {@link Optional} containing the user if found, or an empty {@link Optional}
     * if no user with the given id exists.
     * </p>
     *
     * @param userId the id of the user to find
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<UserProfile> findById(UUID userId);

    /**
     * Finds a {@link UserProfile} entity by its email.
     * <p>
     * Returns an {@link Optional} containing the user if found, or an empty {@link Optional}
     * if no user with the given email exists.
     * </p>
     *
     * @param email the email of the user to find
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<UserProfile> findByEmail(String email);
}
