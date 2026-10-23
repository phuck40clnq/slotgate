package com.example.auth.application.dto.command;

import lombok.NonNull;

public record RefreshTokenCommand(
    @NonNull String token
) {}
