package com.example.auth.application.port.in;

import com.example.auth.application.dto.command.RegisterUserCommand;
import com.example.auth.application.dto.result.RegisterUserResult;

public interface RegisterUserUseCase {
    public RegisterUserResult register(RegisterUserCommand command);
}
