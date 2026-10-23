package com.example.user.application.dto;

import java.util.UUID;

import lombok.NonNull;

public record UpdateUserProfileCommand(
    @NonNull UUID userId,
    @NonNull String fullName,
    @NonNull String avatarUrl
) {}
