package com.example.auth.adapter.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.adapter.web.dto.*;
import com.example.auth.application.dto.command.*;
import com.example.auth.application.dto.result.*;
import com.example.auth.application.port.in.*;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for authentication-related operations.
 * <p>
 * Exposes endpoints for user registration, login, and token refresh.
 * Delegates business logic to the corresponding use case classes.
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase    loginUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user credentials
     * @return a {@link TokenResponse} with the access token
     */
    @PostMapping("/register")
    public TokenResponse register(@RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(request.email(), request.password());
        RegisterUserResult result = registerUserUseCase.register(command);
        return new TokenResponse(result.token());
    }

    /**
     * Authenticates a user and returns an access token.
     *
     * @param request the login request containing user credentials
     * @return a {@link TokenResponse} with the access token
     */
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        LoginUserCommand command = new LoginUserCommand(request.email(), request.password());
        LoginUserResult result = loginUserUseCase.login(command);
        return new TokenResponse(result.token());
    }

    /**
     * Refreshes an existing access token using a refresh token.
     *
     * @param request the refresh token request
     * @return a {@link TokenResponse} with the new access token
     */
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
        RefreshTokenResult result = refreshTokenUseCase.refresh(command);
        return new TokenResponse(result.token());
    }
}
