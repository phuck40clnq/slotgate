package com.example.user.adapter.messaging.handlers;

import com.example.user.application.dto.CreateUserCommand;
import com.example.user.application.port.in.CreateUserFromEventUseCase;
import com.example.user.domain.event.user.UserRegisteredEvent;
import com.example.user.infrastructure.messaging.kafka.stereotype.KafkaComponent;
import com.example.user.infrastructure.messaging.shared.consumer.EventHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@KafkaComponent
public class OnUserRegistered implements EventHandler<UserRegisteredEvent> {

	private final CreateUserFromEventUseCase useCase;

	@Override
	public void handle(UserRegisteredEvent event) {
		CreateUserCommand cmd =
			new CreateUserCommand(
				event.getUserId(),
				event.getEmail(),
				"Default full name"
			);
		useCase.create(cmd);
	}

	@Override
	public Class<UserRegisteredEvent> getEventType() {
		return UserRegisteredEvent.class;
	}

}
