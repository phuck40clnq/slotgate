package com.example.auth.infrastructure.messaging.kafka.producer.mappers.user;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.example.auth.domain.event.user.UserLoggedInEvent;
import com.example.auth.infrastructure.messaging.kafka.producer.KafkaEventMapper;
import com.example.auth.infrastructure.messaging.kafka.schema.EventSchemaProvider;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;

@KafkaComponent
public final class UserLoggedInMapper 
			implements KafkaEventMapper<UserLoggedInEvent> {

    private final Schema schema;
    private final String schemaName = "UserLoggedIn";

    public UserLoggedInMapper(EventSchemaProvider schemaProvider) {
        this.schema = schemaProvider.get(schemaName);
    }

	@Override
    public Class<UserLoggedInEvent> getEventType() {
        return UserLoggedInEvent.class;
    }

    @Override
    public GenericRecord map(UserLoggedInEvent event) {
        GenericRecord record = new GenericData.Record(schema);

        record.put("eventId", event.eventId().toString());
        record.put("occurredAt", event.occurredAt().toEpochMilli());
        record.put("userId", event.getUserId().toString());
        
        return record;
    }
    
}
