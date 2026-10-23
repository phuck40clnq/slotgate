package com.example.auth.infrastructure.messaging.kafka.consumer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaMessage;

import lombok.RequiredArgsConstructor;

@KafkaComponent
@RequiredArgsConstructor
public class KafkaEventListeners {

    private final KafkaMessageTranslatorRegistry translator;
    private final KafkaEventDispatcher dispatcher;

    @KafkaListener(
        topics = {
            "auth-auth-events",
        },
        groupId = "auth-service"
    )
    public void onMessage(GenericRecord record) {

        String eventType = record.getSchema().getFullName();

        KafkaMessage<GenericRecord> message =
            new KafkaMessage<>(eventType, record);

        DomainEvent event = translator.translate(message);
        dispatcher.dispatch(event);
    }
}
