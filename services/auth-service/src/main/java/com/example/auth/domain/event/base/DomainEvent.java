package com.example.auth.domain.event.base;

import java.time.Instant;
import java.util.UUID;

/**
 * Marker interface for all domain events.
 * <p>
 * A {@code DomainEvent} represents something that has occurred in the domain
 * that may be of interest to other parts of the system.
 * </p>
 */
public interface DomainEvent {

    /**
     * Returns the timestamp indicating when the event occurred.
     *
     * @return the {@link Instant} when the event was created
     */
    Instant occurredAt();

    /**
     * Returns the unique event identify of the event.
     * 
     * @return the {@link UUID} of event
     */
    UUID eventId();

}
