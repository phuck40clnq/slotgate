package com.example.user.infrastructure.messaging.shared.producer;

import com.example.user.domain.event.base.DomainEvent;

public interface EventRouter<E extends DomainEvent, R extends EventRoute> {
    
    /**
     * Returns the class of domain event this router supports.
     */
    Class<E> getEventType();

    /**
     * Return routing info for this event.
     */
    R route(E event);

}
