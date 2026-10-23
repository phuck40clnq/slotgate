package com.example.auth.application.port.in;

import com.example.auth.application.dto.command.RefreshTokenCommand;
import com.example.auth.application.dto.result.RefreshTokenResult;

public interface RefreshTokenUseCase {
    public RefreshTokenResult refresh(RefreshTokenCommand command);
}
