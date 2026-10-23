package com.example.user.infrastructure.messaging.shared.producer;

import com.example.user.domain.event.base.DomainEvent;

/**
 * Maps a domain event to a payload.
 *
 * @param <Event> the domain event type
 * @param <Payload> the outbound payload
 */
public interface EventMapper<Event extends DomainEvent, Payload> {

    /**
     * Returns the class of domain event this mapper supports.
     */
    Class<Event> getEventType();

    /**
     * Map the domain event to outbound message.
     *
     * @param event the domain event
     * @return the outbound message
     */
    Payload map(Event event);
}
