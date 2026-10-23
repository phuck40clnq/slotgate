package com.example.auth.infrastructure.messaging.kafka.producer;

import org.apache.avro.generic.GenericRecord;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.shared.producer.EventMapper;

public interface KafkaEventMapper<E extends DomainEvent>
        extends EventMapper<E, GenericRecord> {
}
