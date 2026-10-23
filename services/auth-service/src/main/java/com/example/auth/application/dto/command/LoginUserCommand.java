package com.example.auth.application.dto.command;

import lombok.NonNull;

/**
 * Request DTO for user login.
 * <p>
 * Represents the data sent by the client to authenticate a user.
 * Contains the user's email and password.
 * </p>
 *
 * @param email the user's email address, must not be null
 * @param password the user's password, must not be null
 */
public record LoginUserCommand(
    @NonNull String email,
    @NonNull String password
) {}
