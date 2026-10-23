package com.example.user.application.port.in;

import java.util.UUID;

import com.example.user.application.dto.UserProfileResult;

public interface GetUserProfileUseCase {
    public UserProfileResult get(UUID userId);
}
