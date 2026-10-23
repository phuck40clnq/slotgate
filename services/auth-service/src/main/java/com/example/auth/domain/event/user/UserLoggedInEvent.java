package com.example.auth.domain.event.user;

import java.util.UUID;

import lombok.Getter;

/**
 * Event that represents a user successfully logging into the system.
 * <p>
 * This event extends {@link UserDomainEvent} and therefore carries the {@code userId}
 * of the user who logged in, as well as the timestamp of when the event occurred.
 * </p>
 * <p>
 * It can be used by event handlers to trigger actions such as auditing, notifications,
 * or updating user session state.
 * </p>
 */
@Getter
public class UserLoggedInEvent extends UserDomainEvent {

    /**
     * Creates a new {@code UserLoggedInEvent} for the specified user.
     *
     * @param userId the UUID of the user who logged in
     */
    public UserLoggedInEvent(UUID userId) {
        super(userId);
    }

}
