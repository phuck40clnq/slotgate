package com.example.user.infrastructure.messaging.kafka.schema;

import org.apache.avro.Schema;

import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;

@KafkaComponent
public class ResourceEventSchemaProvider implements EventSchemaProvider {
    
    private final AvroResourceSchemaLoader loader = 
            new AvroResourceSchemaLoader();

    @Override
    public Schema get(String eventName) {
        return loader.load(eventName);
    }

}