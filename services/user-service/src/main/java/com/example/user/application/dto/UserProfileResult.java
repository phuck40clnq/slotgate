package com.example.user.application.dto;

import java.util.UUID;

import com.example.user.domain.model.UserProfile;

import lombok.NonNull;

public record UserProfileResult(
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull String fullName,
    @NonNull String status
) {
    public static UserProfileResult of(UserProfile user) {
        return new UserProfileResult(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getStatus().name()
        );
    }
}
