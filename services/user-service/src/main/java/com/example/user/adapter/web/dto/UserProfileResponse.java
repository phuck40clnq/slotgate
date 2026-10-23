package com.example.user.adapter.web.dto;

import java.util.UUID;

import lombok.NonNull;

public record UserProfileResponse(
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull String fullName,
    @NonNull String status
) {}
