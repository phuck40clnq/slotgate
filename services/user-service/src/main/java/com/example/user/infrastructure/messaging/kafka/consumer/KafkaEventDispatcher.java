package com.example.user.infrastructure.messaging.kafka.consumer;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.user.infrastructure.messaging.shared.consumer.EventDispatcher;

import lombok.RequiredArgsConstructor;

@KafkaComponent
@RequiredArgsConstructor
public class KafkaEventDispatcher implements EventDispatcher {

    private final EventHandlerRegistry handlerRegistry;

    @Override
    public void dispatch(DomainEvent event) {
        handlerRegistry.get(event).handle(event);
    }
    
}
