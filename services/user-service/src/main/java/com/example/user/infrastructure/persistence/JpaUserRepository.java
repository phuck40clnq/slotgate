package com.example.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link JpaUserEntity}.
 * <p>
 * Provides CRUD operations and custom queries for user persistence.
 * Extends {@link JpaRepository} to inherit standard database operations.
 * </p>
 */
public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    /**
     * Finds a user entity by its email address.
     * <p>
     * Returns an {@link Optional} containing the user if found, or an empty
     * {@link Optional} if no user with the given email exists.
     * </p>
     *
     * @param email the email of the user to find
     * @return an {@link Optional} containing the user entity if found, otherwise empty
     */
    Optional<JpaUserEntity> findByEmail(String email);

    /**
     * Finds a user entity by its id.
     * <p>
     * Returns an {@link Optional} containing the user if found, or an empty
     * {@link Optional} if no user with the given email exists.
     * </p>
     *
     * @param userId the id of the user to find
     * @return an {@link Optional} containing the user entity if found, otherwise empty
     */
    Optional<JpaUserEntity> findById(UUID userId);

}
