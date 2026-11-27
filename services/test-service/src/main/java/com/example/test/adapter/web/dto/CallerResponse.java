package com.example.test.adapter.web.dto;

import java.util.Objects;
import java.util.UUID;

public record CallerResponse(UUID userId) {

    public CallerResponse {
        Objects.requireNonNull(userId, "userId");
    }
}
