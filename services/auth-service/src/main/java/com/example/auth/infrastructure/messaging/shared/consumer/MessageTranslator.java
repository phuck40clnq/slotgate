package com.example.auth.infrastructure.messaging.shared.consumer;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.shared.utils.Message;

/**
 * Translates raw messages into domain events.
 *
 * @param <M> the message type extends from {@link Message}
 * @param <Event> the event extends from {@link DomainEvent}
 */
public interface MessageTranslator<M extends Message, Event extends DomainEvent> {

    /**
     * Helper function to find suitable 
     * translator with each event type
     */
    String getMessageFullName();

    /**
     * Translate the raw message into a domain event.
     *
     * @param message the raw message
     * @return the event extends from {@link DomainEvent}
     */
    Event translate(M message);
}
