package com.example.auth.infrastructure.messaging.shared.producer;

import com.example.auth.domain.event.base.DomainEvent;

/**
 * Maps a domain event to an outbound message.
 *
 * @param <E> the domain event type
 * @param <Payload> the payload type
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
