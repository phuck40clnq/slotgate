package com.example.auth.infrastructure.messaging.kafka.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;

import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.auth.infrastructure.messaging.shared.utils.AbstractResourceSchemaLoader;

@KafkaComponent
public class AvroResourceSchemaLoader 
        extends AbstractResourceSchemaLoader<Schema> {

    private final static String basePath = "contracts/avro";

    public AvroResourceSchemaLoader() {
        super(basePath);
    }

    @Override
    protected Schema parse(InputStream inputStream) throws IOException {
        return new Schema.Parser().parse(inputStream);
    }

    @Override
    protected String resolvePath(String schemaName) {
        return basePath + "/" + schemaName + ".avsc";
    }
    
}
