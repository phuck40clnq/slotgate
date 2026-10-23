package com.example.user.infrastructure.messaging.kafka.schema;

import org.apache.avro.Schema;

public interface EventSchemaProvider {
    
    /**
     * Get Avro Schema by default
     * This is the port to separate {@link }
     * 
     * @param eventName
     * @return
     */
    Schema get(String eventName);

}
