package com.example.user.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.user.infrastructure.messaging.kafka.consumer.EventHandlerRegistry;
import com.example.user.infrastructure.messaging.kafka.consumer.KafkaMessageTranslatorRegistry;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.user.infrastructure.messaging.shared.consumer.EventHandler;
import com.example.user.infrastructure.messaging.shared.consumer.MessageTranslator;


@Configuration
public class MessagingConsumerConfig {

    @Bean
    public EventHandlerRegistry eventHandlerRegistry(
            @Qualifier(KafkaComponent.NAME)
            List<EventHandler<?>> handlers) {
        return new EventHandlerRegistry(handlers);
    }

    @Bean
    public KafkaMessageTranslatorRegistry eventTranslatorRegistry(
            @Qualifier(KafkaComponent.NAME)
            List<MessageTranslator<?, ?>> translators) {

        return new KafkaMessageTranslatorRegistry(translators);
    }
}
