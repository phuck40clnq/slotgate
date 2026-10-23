package com.example.auth.domain.event.user;

import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

@Getter
public class UserRegisteredEvent extends UserDomainEvent {

    private final String email;

    public UserRegisteredEvent(UUID userId, String email) {
        super(userId);
        this.email = Objects.requireNonNull(email);
    }

}
