package com.example.user.adapter.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.adapter.web.dto.*;
import com.example.user.application.dto.*;
import com.example.user.application.port.in.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;

    @GetMapping("/me/profile")
    public UserProfileResponse getProfile(
        @RequestHeader("X-Internal-User-Id") UUID userId
    ) {
        UserProfileResult result = getUserProfileUseCase.get(userId);
        UserProfileResponse response = 
            new UserProfileResponse(
                userId, result.email(), result.fullName(), result.status());
        return response;
    }

    @PutMapping("/me/profile/edit")
    public UserProfileResponse updateProfile(
        @RequestHeader("X-Internal-User-Id") UUID userId,
        @RequestBody UpdateProfileRequest request
    ) {
        UpdateUserProfileCommand command
            = new UpdateUserProfileCommand(
                userId,
                request.fullName(),
                request.avatarUrl()
            );
            
        UserProfileResult result = updateUserProfileUseCase.update(command);
        UserProfileResponse response = 
            new UserProfileResponse(
                userId, result.email(), result.fullName(), result.status());
        return response;
    }

}