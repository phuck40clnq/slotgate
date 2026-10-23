package com.example.auth.application.dto.result;

import lombok.NonNull;

public record RegisterUserResult (
    @NonNull String token
) {}
