package com.example.auth.infrastructure.messaging.kafka.producer.routers.user;

import com.example.auth.domain.event.user.UserRegisteredEvent;
import com.example.auth.infrastructure.messaging.kafka.producer.KafkaEventRoute;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.auth.infrastructure.messaging.shared.producer.EventRouter;

@KafkaComponent
public class UserRegisteredRouter
            implements EventRouter<UserRegisteredEvent, KafkaEventRoute> {

    @Override
    public Class<UserRegisteredEvent> getEventType() {
        return UserRegisteredEvent.class;
    }

    @Override
    public KafkaEventRoute route(UserRegisteredEvent event) {
        return new KafkaEventRoute(
            "auth-user-events",
            event.getUserId().toString()
        );
    }

}
