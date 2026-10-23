package com.example.user.infrastructure.messaging.kafka.consumer;

import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@KafkaComponent
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListeners {

    private final KafkaMessageTranslatorRegistry translator;
    private final KafkaEventDispatcher dispatcher;

    @KafkaListener(
        topics = {
            "auth-user-events",
        },
        groupId = "user-service"
    )
    public void onMessage(
        @Payload GenericRecord record,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {

        String eventType = record.getSchema().getFullName();
        
        try {
            KafkaMessage<GenericRecord> message =
                new KafkaMessage<>(eventType, record);

            DomainEvent event = translator.translate(message);
            dispatcher.dispatch(event);
        } catch (Exception ex) {
            
            // Log error
            log.error(
                "Kafka consume failed",
                Map.of(
                    "topic", topic,
                    "partition", partition,
                    "offset", offset,
                    "eventType", eventType
                )
            );

            // Rethrow original exception
            throw ex;
        }
    }
}
