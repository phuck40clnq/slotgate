package com.example.user.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing the {@link UserProfile} domain object for persistence.
 * <p>
 * This class maps the {@code User} fields to the database table {@code users}.
 * It provides conversion from and to the domain {@link UserProfile} object, allowing
 * separation between the persistence layer and the domain layer.
 * </p>
 */
@Entity
@Table(name = "users")
public class JpaUserEntity {

    /** Primary key of the user. */
    @Id
    private UUID id;

    /** User's email, must be unique and not null. */
    @Column(nullable = false, unique = true)
    private String email;

    /** User's full name. */
    private String fullName;

    /** URL to the user's avatar image. */
    private String avatarUrl;

    /** Status of the user. Stored as string in the database. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /** Timestamp when the user was created. Not updatable after insert. */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Timestamp when the user was last updated. */
    private Instant updatedAt;

    /**
     * Default constructor required by Hibernate.
     */
    protected JpaUserEntity() {}

    /**
     * Creates a new JPA entity from a domain {@link UserProfile} object.
     *
     * @param user the domain user to convert
     */
    public JpaUserEntity(UserProfile user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.avatarUrl = user.getAvatarUrl();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    /**
     * Converts this JPA entity back to a domain {@link UserProfile} object.
     *
     * @return the corresponding domain user
     */
    public UserProfile toDomain() {
        return new UserProfile(
            this.id,
            this.email,
            this.fullName,
            this.avatarUrl,
            this.status,
            this.createdAt,
            this.updatedAt
        );
    }
}
