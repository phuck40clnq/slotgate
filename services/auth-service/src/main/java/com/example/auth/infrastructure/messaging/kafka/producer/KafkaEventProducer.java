package com.example.auth.infrastructure.messaging.kafka.producer;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.auth.application.port.out.EventProducerPort;
import com.example.auth.common.error.AuthInfrasErrorCode;
import com.example.auth.common.exception.InfrastructureException;
import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;

import lombok.RequiredArgsConstructor;

@KafkaComponent
@RequiredArgsConstructor
public class KafkaEventProducer implements EventProducerPort {
    
    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;
    private final KafkaEventRouterRegistry             router;
    private final KafkaEventMapperRegistry             mapper;

    @Override
    public void produce(DomainEvent event) {
        try {
            KafkaEventRoute route = router.route(event);
            GenericRecord payload = mapper.map(event);
            kafkaTemplate.send(route.topic(), route.key(), payload);
        } catch (Exception ex) {
            throw new InfrastructureException(
                AuthInfrasErrorCode.KAFKA_PRODUCE_FAILED,
                Map.of(
                    "event", event.getClass().getSimpleName()
                ),
                ex
            );
        }
    }

}
