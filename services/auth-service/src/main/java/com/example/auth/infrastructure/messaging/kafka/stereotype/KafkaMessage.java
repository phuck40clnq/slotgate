package com.example.auth.infrastructure.messaging.kafka.stereotype;

import com.example.auth.infrastructure.messaging.shared.utils.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a Kafka message wrapper used at the infrastructure layer.
 * <p>
 * This class encapsulates:
 * <ul>
 *   <li>The fully qualified name of the Avro schema associated with the message</li>
 *   <li>The raw payload deserialized from Kafka</li>
 * </ul>
 *
 * <p>
 * The {@code fullName} typically corresponds to
 * {@link org.apache.avro.Schema#getFullName()} and is used as a
 * technical message identifier for routing, translation, or dispatching
 * to the appropriate domain event handler.
 *
 * <p>
 * This class is intentionally infrastructure-specific and should not be
 * exposed directly to the domain layer.
 *
 * @param <T> the type of the message payload
 */
@Getter
@RequiredArgsConstructor
public class KafkaMessage<T> implements Message {

    /**
     * Fully qualified name of the Avro schema associated with this message.
     * <p>
     * Example:
     * <pre>
     * "com.example.auth.events.UserRegistered"
     * </pre>
     */
    private final String fullName;

    /**
     * The raw message payload deserialized from Kafka.
     */
    private final T payload;

}
