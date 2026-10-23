package com.example.user.application.dto;

import java.util.UUID;

import lombok.NonNull;

public record CreateUserCommand (
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull String fullName
) {}
