package com.example.user.infrastructure.messaging.shared.consumer;

import com.example.user.domain.event.base.DomainEvent;

/**
 * Handles a specific type of domain event.
 *
 * @param <E> the domain event type
 */
public interface EventHandler<E extends DomainEvent> {

    /**
     * Returns the class of the event this handler handles.
     */
    Class<E> getEventType();

    /**
     * Handle the domain event.
     *
     * @param event the event to handle
     */
    void handle(E event);
}
