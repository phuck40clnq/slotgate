package com.example.user.infrastructure.messaging.kafka.producer;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.user.application.port.out.EventProducer;
import com.example.user.common.error.UserInfrasErrorCode;
import com.example.user.common.exception.InfrastructureException;
import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link EventProducer} that publishes {@link DomainEvent}s for messaging.
 * <p>
 * This class uses a {@link KafkaTemplate} to send events and an {@link KafkaEventRouter}
 * to determine the topic and key for each event.
 * </p>
 */
@KafkaComponent
@RequiredArgsConstructor
public class KafkaEventProducer implements EventProducer {

    /** Router used to determine topic and key for events. */
    private final KafkaEventRouterRegistry router;
    
    /** Mapper used to map the event to type messaging */
    private final KafkaEventMapperRegistry mapper;

    /** Kafka template used to send messages. */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(DomainEvent event) {
        try {
            KafkaEventRoute route = router.route(event);
            GenericRecord payload = mapper.map(event);
            kafkaTemplate.send(route.topic(), route.key(), payload);
        } catch (Exception ex) {
            throw new InfrastructureException(
                UserInfrasErrorCode.KAFKA_PRODUCE_FAILED,
                Map.of(
                    "event", event.getClass().getSimpleName()
                ),
                ex
            );
        }
    }
}
