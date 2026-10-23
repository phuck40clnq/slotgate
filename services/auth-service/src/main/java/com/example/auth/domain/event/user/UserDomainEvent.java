package com.example.auth.domain.event.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.example.auth.domain.event.base.AbstractDomainEvent;

import lombok.Getter;

/**
 * Base class for all domain events related to {@link com.example.user.domain.model.User}.
 * <p>
 * This class extends {@link AbstractDomainEvent} and adds a {@code userId} field
 * to identify the user associated with the event.
 * </p>
 * <p>
 * All user-related domain events should extend this class to inherit the timestamp
 * and user identification behavior.
 * </p>
 */
@Getter
public abstract class UserDomainEvent extends AbstractDomainEvent {

    /**
     * The unique identifier of the user associated with this event.
     */
    private final UUID userId;

    /**
     * Creates a new user domain event with the given user ID.
     *
     * <p>
     * This method is helpful for translator to
     * create event from available data.
     * </p>
     * 
     * @param userId the UUID of the user associated with this event
     */
    protected UserDomainEvent(UUID userId) {
        super();
        this.userId = Objects.requireNonNull(userId);
    }

    /**
     * Creates a new user domain event with
     * the specific time, eventId and userId.
     * 
     * @param eventId the UUID of event passed in
     * @param occurredAt the specific time variable passed in
     * @param userId the UUID of the user associated with this event
     */
    protected UserDomainEvent(UUID eventId, Instant occurredAt, UUID userId) {
        super(eventId, occurredAt);
        this.userId = Objects.requireNonNull(userId);
    }

}
