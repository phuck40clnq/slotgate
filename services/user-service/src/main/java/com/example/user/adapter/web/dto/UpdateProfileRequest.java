package com.example.user.adapter.web.dto;

public record UpdateProfileRequest(
    String fullName,
    String avatarUrl
) {}
