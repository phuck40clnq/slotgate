package com.example.user.infrastructure.messaging.shared.consumer;

import com.example.user.domain.event.base.DomainEvent;

/**
 * Defines a dispatcher for {@link DomainEvent}s.
 * <p>
 * Implementations of this interface are responsible for routing domain events
 * to their corresponding handlers. This is typically used in an event-driven
 * architecture to decouple the production of events from their processing.
 * </p>
 */
public interface EventDispatcher {

    /**
     * Dispatches the given {@link DomainEvent} to its appropriate handler(s).
     * <p>
     * Implementations should ensure that the event is delivered to the correct
     * handler, and handle the case where no handler is registered for a given event.
     * </p>
     *
     * @param event the domain event to dispatch
     * @throws IllegalArgumentException if no handler is available for the event
     */
    void dispatch(DomainEvent event);
}
