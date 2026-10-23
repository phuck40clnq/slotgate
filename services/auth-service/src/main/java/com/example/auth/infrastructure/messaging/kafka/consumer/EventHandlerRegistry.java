package com.example.auth.infrastructure.messaging.kafka.consumer;

import java.util.List;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.shared.consumer.EventHandler;
import com.example.auth.infrastructure.messaging.shared.utils.AbstractTypeRegistry;

public class EventHandlerRegistry
        extends AbstractTypeRegistry<Class<?>, EventHandler<?>> {

    public EventHandlerRegistry(
        List<EventHandler<?>> handlerList
    ) {
        super(handlerList, EventHandler::getEventType);
    }

    @SuppressWarnings("unchecked")
    public <E extends DomainEvent> EventHandler<E> get(E event) {
        return (EventHandler<E>) getRequired(
            event.getClass(),
            "EventHandler"
        );
    }

}
