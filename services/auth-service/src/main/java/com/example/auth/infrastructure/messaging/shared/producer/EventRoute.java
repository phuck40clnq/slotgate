package com.example.auth.infrastructure.messaging.shared.producer;

import java.util.Map;

public interface EventRoute {
    /**
     * Returns dynamic routing metadata for this event.
     * Could be topic/key (Kafka), URL (HTTP), queue/exchange (RabbitMQ), etc.
     */
    Map<String, Object> metadata();
}