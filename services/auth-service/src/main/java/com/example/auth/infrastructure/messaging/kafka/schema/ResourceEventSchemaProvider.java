package com.example.auth.infrastructure.messaging.kafka.schema;

import org.apache.avro.Schema;

import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;

import lombok.RequiredArgsConstructor;

@KafkaComponent
@RequiredArgsConstructor
public class ResourceEventSchemaProvider implements EventSchemaProvider {
    
    private final AvroResourceSchemaLoader loader;

    @Override
    public Schema get(String eventName) {
        return loader.load(eventName);
    }

}