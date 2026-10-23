package com.example.auth.application.dto.result;

import lombok.NonNull;

public record LoginUserResult(
    @NonNull String token
) {}
