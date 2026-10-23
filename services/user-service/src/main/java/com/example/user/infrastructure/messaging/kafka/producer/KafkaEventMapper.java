package com.example.user.infrastructure.messaging.kafka.producer;

import org.apache.avro.generic.GenericRecord;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.shared.producer.EventMapper;

public interface KafkaEventMapper<E extends DomainEvent>
        extends EventMapper<E, GenericRecord> {
}
