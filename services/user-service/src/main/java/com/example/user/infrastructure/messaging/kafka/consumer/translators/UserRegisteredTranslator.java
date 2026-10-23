package com.example.user.infrastructure.messaging.kafka.consumer.translators;

import java.time.Instant;
import java.util.UUID;

import org.apache.avro.generic.GenericRecord;

import com.example.user.domain.event.user.UserRegisteredEvent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaMessage;
import com.example.user.infrastructure.messaging.shared.consumer.MessageTranslator;

@KafkaComponent
public class UserRegisteredTranslator
        implements MessageTranslator<KafkaMessage<GenericRecord>, UserRegisteredEvent> {

    @Override
    public String getMessageFullName() {
        return "com.example.auth.events.UserRegistered";
    }

    @Override
    public UserRegisteredEvent translate(KafkaMessage<GenericRecord> message) {
        var record = message.getPayload();

        UserRegisteredEvent event =  new UserRegisteredEvent(
            UUID.fromString(record.get("eventId").toString()),
            Instant.ofEpochMilli((Long) record.get("occurredAt")),
            UUID.fromString(record.get("userId").toString()),
            record.get("email").toString()
        );

        return event;
    }
    
}
