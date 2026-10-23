package com.example.auth.infrastructure.messaging.kafka.producer;

import java.util.Map;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.shared.producer.EventRoute;

/**
 * Represents routing information for a {@link DomainEvent} in a messaging system.
 * <p>
 * Each {@code Route} defines a Kafka topic and a key function to determine
 * the message key for a given domain event. The key is typically used for
 * partitioning or ordering messages in Kafka.
 * </p>
 *
 * @param topic the Kafka topic name to which the event should be published
 * @param key a Kafka key that helps to load balancer when produce {@link DomainEvent}
 */
public record KafkaEventRoute(String topic, String key)
        implements EventRoute {
    
    @Override
    public Map<String, Object> metadata() {
        return Map.of(
            "topic", topic,
            "key", key
        );
    }

}
