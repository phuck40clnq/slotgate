package com.example.user.application.port.in;

import com.example.user.application.dto.CreateUserCommand;

public interface CreateUserFromEventUseCase {
    public void create(CreateUserCommand command);
}
