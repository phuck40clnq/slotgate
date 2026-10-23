package com.example.user.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a user in the system.
 * <p>
 * This class models the core user entity in the domain layer, including user identity,
 * contact information, status, timestamps, and business operations.
 * </p>
 * <p>
 * The class provides methods to update user information and manage user status
 * while automatically updating the {@code updatedAt} timestamp.
 * </p>
 */
@AllArgsConstructor
@Getter
public class UserProfile {

    /** Unique identifier for the user. */
    @NonNull
    private final UUID id;

    /** User's email address. */
    @NonNull
    private String email;

    /** User's full name. */
    @NonNull
    private String fullName;

    /** Optional URL to the user's avatar image. */
    private String avatarUrl;

    /** Status of the user (e.g., ACTIVE, DELETED). */
    @NonNull
    private UserStatus status;

    /** Timestamp when the user was created. */
    @NonNull
    private final Instant createdAt;

    /** Timestamp when the user was last updated. */
    @NonNull
    private Instant updatedAt;

    /**
     * Creates a new user with the specified ID, email, and full name.
     * <p>
     * The status is initialized to {@link UserStatus#ACTIVE}, and timestamps
     * {@code createdAt} and {@code updatedAt} are set to the current instant.
     * </p>
     *
     * @param id       the unique identifier of the user
     * @param email    the user's email address
     * @param fullName the user's full name
     */
    public UserProfile(@NonNull UUID id, @NonNull String email, @NonNull String fullName) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.fullName = fullName;
        this.status = UserStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // -----------------------
    // Business methods
    // -----------------------

    /**
     * Deactivates the user by setting the status to {@link UserStatus#DELETED}.
     * <p>
     * The {@code updatedAt} timestamp is also updated to the current instant.
     * </p>
     */
    public void deactivate() {
        if (status != UserStatus.DELETED) {
            this.status = UserStatus.DELETED;
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Activates the user by setting the status to {@link UserStatus#ACTIVE}.
     * <p>
     * The {@code updatedAt} timestamp is also updated to the current instant.
     * </p>
     */
    public void activate() {
        if (status != UserStatus.ACTIVE) {
            this.status = UserStatus.ACTIVE;
            this.updatedAt = Instant.now();
        }
    }

    // -----------------------
    // Updating user fields
    // -----------------------

    /**
     * Updates the user's full name.
     * <p>
     * The {@code updatedAt} timestamp is updated automatically.
     * </p>
     *
     * @param fullName the new full name, must not be null
     */
    public void updateFullName(@NonNull String fullName) {
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the user's avatar URL.
     * <p>
     * The {@code updatedAt} timestamp is updated automatically.
     * </p>
     *
     * @param avatarUrl the new avatar URL, can be null
     */
    public void updateAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }
}
