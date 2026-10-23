package com.example.user.domain.model;

/**
 * Represents the status of a {@link UserProfile}.
 * <p>
 * The status indicates whether a user is active and can access the system,
 * or has been deleted/deactivated and should no longer be considered active.
 * </p>
 */
public enum UserStatus {

    /** The user is active and can perform normal operations. */
    ACTIVE,

    /** The user has been deleted or deactivated and should no longer be active. */
    DELETED
}
