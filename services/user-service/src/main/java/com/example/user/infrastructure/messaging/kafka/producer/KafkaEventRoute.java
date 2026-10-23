package com.example.user.infrastructure.messaging.kafka.producer;

import java.util.Map;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.shared.producer.EventRoute;

/**
 * Represents routing information for a {@link DomainEvent} in a messaging system.
 * <p>
 * Each {@code Route} defines a Kafka topic and a key function to determine
 * the message key for a given domain event. The key is typically used for
 * partitioning or ordering messages in Kafka.
 * </p>
 *
 * @param topic the {@link String} Kafka topic name to which the event should be published
 * @param key a {@link String} key that computes the message key from a {@link DomainEvent}
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
