package com.example.auth.infrastructure.messaging.kafka.consumer;

import java.util.List;

import com.example.auth.domain.event.base.DomainEvent;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaMessage;
import com.example.auth.infrastructure.messaging.shared.consumer.MessageTranslator;
import com.example.auth.infrastructure.messaging.shared.utils.AbstractTypeRegistry;

public class KafkaMessageTranslatorRegistry
        extends AbstractTypeRegistry<String, MessageTranslator<?, ?>> {

    public KafkaMessageTranslatorRegistry(List<MessageTranslator<?, ?>> translators) {
        super(
            translators,
            MessageTranslator::getMessageFullName
        );
    }

    public DomainEvent translate(KafkaMessage<?> message) {
        MessageTranslator<KafkaMessage<?>, ?> translator =
            this.getTranslator(message.getFullName());

        return translator.translate(message);
    }
    
    @SuppressWarnings("unchecked")
    private MessageTranslator<KafkaMessage<?>, ?> getTranslator(String eventType) {
        return (MessageTranslator<KafkaMessage<?>, ?>)
            getRequired(eventType, "EventTranslator");
    }

}
