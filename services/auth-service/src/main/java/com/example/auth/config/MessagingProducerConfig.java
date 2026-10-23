package com.example.auth.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.example.auth.infrastructure.messaging.kafka.producer.*;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.auth.infrastructure.messaging.shared.producer.EventRouter;

@Configuration
@ComponentScan(
    basePackages = "com.example.auth.infrastructure.messaging.kafka.producer"
)
public class MessagingProducerConfig {

    @Bean
    public KafkaEventMapperRegistry eventMapperRegistry(
            @Qualifier(KafkaComponent.NAME)
            List<KafkaEventMapper<?>> mappers) {
        return new KafkaEventMapperRegistry(mappers);
    }

    @Bean
    public KafkaEventRouterRegistry eventRouterRegistry(
            @Qualifier(KafkaComponent.NAME)
            List<EventRouter<?, ?>> routers) {
        return new KafkaEventRouterRegistry(routers);
    }
    
}
