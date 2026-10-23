package com.example.user.infrastructure.messaging.kafka.producer;

import java.util.List;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.shared.producer.EventRoute;
import com.example.user.infrastructure.messaging.shared.producer.EventRouter;
import com.example.user.infrastructure.messaging.shared.utils.AbstractTypeRegistry;

public class KafkaEventRouterRegistry
            extends AbstractTypeRegistry<Class<?>, EventRouter<?, ?>> {

    public KafkaEventRouterRegistry(List<EventRouter<?, ?>> routers) {
        super(routers, EventRouter::getEventType);
    }

    public <E extends DomainEvent, R extends EventRoute> R route(E event) {
        EventRouter<E, R> router = this.get(event);
        return router.route(event);
    }

	@SuppressWarnings("unchecked")
    private <E extends DomainEvent, R extends EventRoute> EventRouter<E, R> get(E event) {
        return (EventRouter<E, R>) getRequired(
            event.getClass(),
            "EventRouter"
        );
    }
    
}
