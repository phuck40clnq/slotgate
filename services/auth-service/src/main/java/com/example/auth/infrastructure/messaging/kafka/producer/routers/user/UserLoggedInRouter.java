package com.example.auth.infrastructure.messaging.kafka.producer.routers.user;

import com.example.auth.domain.event.user.UserLoggedInEvent;
import com.example.auth.infrastructure.messaging.kafka.producer.KafkaEventRoute;
import com.example.auth.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.auth.infrastructure.messaging.shared.producer.EventRouter;

@KafkaComponent
public class UserLoggedInRouter 
            implements EventRouter<UserLoggedInEvent, KafkaEventRoute> {

    @Override
    public Class<UserLoggedInEvent> getEventType() {
        return UserLoggedInEvent.class;
    }

    @Override
    public KafkaEventRoute route(UserLoggedInEvent event) {
        return new KafkaEventRoute(
            "auth-security-events",
            event.getUserId().toString()
        );
    }
    
}
