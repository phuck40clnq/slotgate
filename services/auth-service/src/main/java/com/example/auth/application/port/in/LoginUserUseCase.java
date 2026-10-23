package com.example.auth.application.port.in;

import com.example.auth.application.dto.command.LoginUserCommand;
import com.example.auth.application.dto.result.LoginUserResult;

public interface LoginUserUseCase {
    public LoginUserResult login(LoginUserCommand command);
}
