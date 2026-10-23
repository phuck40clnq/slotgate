package com.example.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.example.auth.application.dto.result.LoginUserResult;
import com.example.auth.application.port.in.LoginUserUseCase;
import com.example.auth.application.port.out.AuthUserRepositoryPort;
import com.example.auth.application.port.out.EventProducerPort;
import com.example.auth.application.port.out.PasswordVerifierPort;
import com.example.auth.application.port.out.TokenProviderPort;
import com.example.auth.common.error.AuthBusinessErrorCode;
import com.example.auth.common.exception.BusinessException;
import com.example.auth.application.dto.command.LoginUserCommand;
import com.example.auth.domain.event.user.UserLoggedInEvent;
import com.example.auth.domain.model.AuthUser;
import com.example.auth.domain.security.DomainTokenSubject;

import lombok.RequiredArgsConstructor;

/**
 * Use case for authenticating a user.
 * <p>
 * This use case handles the login flow, including:
 * <ul>
 *   <li>Validating the login request</li>
 *   <li>Verifying user credentials</li>
 *   <li>Publishing a {@link UserLoggedInEvent}</li>
 *   <li>Returning an authentication token</li>
 * </ul>
 * </p>
 */
@RequiredArgsConstructor
@Service
public class LoginUserService implements LoginUserUseCase {

    private final AuthUserRepositoryPort repository;
    private final PasswordVerifierPort passwordVerifier;
    private final EventProducerPort eventProducer;
    private final TokenProviderPort tokenProvider;

    /**
     * Authenticates a user using the provided login command.
     * <p>
     * If the credentials are valid, a {@link UserLoggedInEvent} is published and
     * an authentication token is returned.
     * </p>
     *
     * @param command the login command containing email and password
     * @return a {@link LoginUserResult} containing the authentication token
     * @throws BusinessException with code {@link AuthBusinessErrorCode#INVALID_LOGIN_COMMAND}
     *  if the credentials are invalid
     */
    @Override
    public LoginUserResult login(LoginUserCommand command) {
        // Step 1: Validate command
        this.validateCommand(command);

        // Step 2: Validate user and password
        AuthUser user = this.authenticate(command);

        // Step 3: Publish domain event
        eventProducer.produce(new UserLoggedInEvent(user.getId()));

        // Step 4: Return token
        DomainTokenSubject subject = new DomainTokenSubject(
            user.getId()
        );

        String token = tokenProvider.issueAccessToken(subject);
        return new LoginUserResult(token);
    }

    /**
     * Validates the login command.
     *
     * @param command the login command to validate
     * @throws BusinessException with code {@link AuthBusinessErrorCode#INVALID_LOGIN_COMMAND}
     * if the command is null or contains invalid data
     */
    // TODO: Separate concern of responsibilities
    private void validateCommand(LoginUserCommand command) {
        if (command == null
                || command.email() == null || command.email().isBlank()
                || command.password() == null || command.password().isBlank()) {
            throw new BusinessException(
                AuthBusinessErrorCode.INVALID_LOGIN_COMMAND
            );
        }
    }

    /**
     * Authenticates a user using the provided login command.
     * 
     * <p>
     * This method verifies that user exists and that provided
     * password matches the stored credentials
     * </p>
     * 
     * @param command the login command containing email and password
     * 
     * @return the authenticated {@link AuthUser}
     * 
     * @throws BusinessException with code {@link AuthBusinessErrorCode#INVALID_CREDENTIALS}
     * if the email does not exist or the password is invalid or password does not match
     */
    private AuthUser authenticate(LoginUserCommand command) {
        return this.repository.findByEmail(command.email())
            .filter(user -> passwordVerifier.matches(
            command.password(), user.getPasswordHash()
        ))
        .orElseThrow(() -> new BusinessException(
            AuthBusinessErrorCode.INVALID_CREDENTIALS
        ));
    }
}
