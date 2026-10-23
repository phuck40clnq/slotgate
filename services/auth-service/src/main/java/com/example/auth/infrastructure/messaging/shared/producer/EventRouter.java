package com.example.auth.infrastructure.messaging.shared.producer;

import com.example.auth.domain.event.base.DomainEvent;

public interface EventRouter<Event extends DomainEvent, Route extends EventRoute> {
    
    /**
     * Returns the class of domain event this router supports.
     */
    Class<Event> getEventType();

    /**
     * Return routing info for this event.
     */
    Route route(Event event);

}
