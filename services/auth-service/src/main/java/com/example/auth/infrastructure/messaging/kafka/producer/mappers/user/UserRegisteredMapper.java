package com.example.auth.infrastructure.messaging.kafka.producer.mappers.user;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.example.auth.domain.event.user.UserRegisteredEvent;
import com.example.auth.infrastructure.messaging.kafka.producer.KafkaEventMapper;
import com.example.auth.infrastructure.messaging.kafka.schema.EventSchemaProvider;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;

@KafkaComponent
public final class UserRegisteredMapper 
        implements KafkaEventMapper<UserRegisteredEvent> {

    private final Schema schema;
    private final String schemaName = "UserRegistered";

    public UserRegisteredMapper(EventSchemaProvider schemaProvider) {
        this.schema = schemaProvider.get(schemaName);
    }

	@Override
    public Class<UserRegisteredEvent> getEventType() {
        return UserRegisteredEvent.class;
    }

    @Override
    public GenericRecord map(UserRegisteredEvent event) {
        GenericRecord record = new GenericData.Record(schema);

        record.put("eventId", event.eventId().toString());
        record.put("occurredAt", event.occurredAt().toEpochMilli());
        record.put("userId", event.getUserId().toString());
        record.put("email", event.getEmail());
        
        return record;
    }

}
