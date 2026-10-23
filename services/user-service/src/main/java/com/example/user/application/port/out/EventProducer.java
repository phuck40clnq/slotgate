package com.example.user.application.port.out;

import com.example.user.domain.event.base.DomainEvent;

public interface EventProducer {
    void produce(DomainEvent event);
}
