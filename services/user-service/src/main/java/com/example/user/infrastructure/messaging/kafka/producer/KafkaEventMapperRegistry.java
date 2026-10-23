package com.example.user.infrastructure.messaging.kafka.producer;

import java.util.List;

import org.apache.avro.generic.GenericRecord;

import com.example.user.domain.event.base.DomainEvent;
import com.example.user.infrastructure.messaging.shared.utils.AbstractTypeRegistry;

public class KafkaEventMapperRegistry extends 
            AbstractTypeRegistry<Class<?>, KafkaEventMapper<?>> {

    public KafkaEventMapperRegistry(List<KafkaEventMapper<?>> mapperList) {
        super(mapperList, KafkaEventMapper::getEventType);
    }

    public <E extends DomainEvent> GenericRecord map(E event) {
        KafkaEventMapper<E> mapper = this.get(event);
        return mapper.map(event);
    }

    @SuppressWarnings("unchecked")
	private <E extends DomainEvent> KafkaEventMapper<E> get(E event) {
        return (KafkaEventMapper<E>) getRequired(
            event.getClass(),
            "EventMapper"
        );
    }
}
