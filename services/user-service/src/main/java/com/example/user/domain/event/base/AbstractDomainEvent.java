package com.example.user.domain.event.base;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;

/**
 * Base implementation of {@link DomainEvent}.
 * <p>
 * This abstract class provides a default implementation for storing the time
 * the event occurred. All domain events can extend this class to inherit this behavior.
 * </p>
 */
@AllArgsConstructor
public abstract class AbstractDomainEvent implements DomainEvent {

    /**
     * The timestamp when the event occurred.
     */
    private final Instant occurredAt;

    /**
     * The unique eventId of event.
     */
    private final UUID eventId;

    /**
     * Creates a new domain event and records the current time as its occurrence time.
     */
    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
    }

    /**
     * Creates a new domain event with the specific time and eventId.
     * 
     * @param eventId the UUID of event passed in
     * @param occurredAt the specific time variable passed in
     */
    protected AbstractDomainEvent(UUID eventId, Instant occurredAt) {
        this.eventId = Objects.requireNonNull(eventId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    /**
     * Returns the timestamp indicating when the event occurred.
     *
     * @return the {@link Instant} when the event was created
     */
    @Override
    public Instant occurredAt() {
        return this.occurredAt;
    }

    /**
     * Returns the event id indicating id of event occurred.
     *
     * @return the {@link UUID} of event
     */
    @Override
    public UUID eventId() {
        return this.eventId;
    }

}
