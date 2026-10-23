package com.example.auth.application.dto.result;

import lombok.NonNull;

public record RefreshTokenResult(
    @NonNull String token
) {}
