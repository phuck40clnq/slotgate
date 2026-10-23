package com.example.user.domain.event.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

/**
 * Event that represents a user successfully registering in the system.
 * <p>
 * This event extends {@link UserDomainEvent} and therefore carries the {@code userId}
 * of the newly registered user, as well as the timestamp of when the registration occurred.
 * </p>
 * <p>
 * It can be used by event handlers to trigger actions such as sending welcome emails,
 * initializing user preferences, or logging registration analytics.
 * </p>
 */
@Getter
public class UserRegisteredEvent extends UserDomainEvent {

    private final String email;

    /**
     * Creates a new {@code UserRegisteredEvent} for the specified user.
     *
     * @param userId the UUID of the user who has registered
     */
    public UserRegisteredEvent(UUID userId, String email) {
        super(userId);
        this.email = Objects.requireNonNull(email);
    }

    /**
     * Creates a new user domain event with
     * the specific time, eventId and userId.
     * 
     * <p>
     * This method is helpful for translator to
     * create event from available data.
     * </p>
     * 
     * @param eventId the UUID of event passed in
     * @param occurredAt the specific time variable passed in
     * @param userId the UUID of the user associated with this event
     * @param email the email of user registered event produced
     */
    public UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UUID userId,
        String email
    ) {
        super(eventId, occurredAt, userId);
        this.email = Objects.requireNonNull(email);
    }

}
