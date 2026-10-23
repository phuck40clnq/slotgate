package com.example.auth.application.port.out;

import com.example.auth.domain.event.base.DomainEvent;

public interface EventProducerPort {
    void produce(DomainEvent event);
}
