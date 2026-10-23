package com.example.auth.adapter.web.dto;

import lombok.NonNull;

/**
 * Response DTO representing an authentication token.
 * <p>
 * Returned by login, registration, or refresh endpoints to the client.
 * Contains the access token used for subsequent authenticated requests.
 * </p>
 */
public record TokenResponse(
    @NonNull String token
) {}
