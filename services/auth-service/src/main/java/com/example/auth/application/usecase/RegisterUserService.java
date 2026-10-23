package com.example.auth.application.usecase;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.auth.application.dto.result.RegisterUserResult;
import com.example.auth.application.port.in.RegisterUserUseCase;
import com.example.auth.application.port.out.AuthUserRepositoryPort;
import com.example.auth.application.port.out.EventProducerPort;
import com.example.auth.application.port.out.PasswordHasherPort;
import com.example.auth.application.port.out.TokenProviderPort;
import com.example.auth.application.dto.command.RegisterUserCommand;
import com.example.auth.common.error.AuthBusinessErrorCode;
import com.example.auth.common.error.AuthInfrasErrorCode;
import com.example.auth.common.exception.BusinessException;
import com.example.auth.common.exception.InfrastructureException;
import com.example.auth.domain.event.user.UserRegisteredEvent;
import com.example.auth.domain.model.AuthUser;
import com.example.auth.domain.security.DomainTokenSubject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Use case for registering a new user.
 * <p>
 * This use case handles the full user registration flow, including:
 * <ul>
 *   <li>Validating the registration request</li>
 *   <li>Checking for existing users</li>
 *   <li>Creating and persisting a new user</li>
 *   <li>Publishing a {@link UserRegisteredEvent}</li>
 *   <li>Returning an authentication token</li>
 * </ul>
 * </p>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RegisterUserService implements RegisterUserUseCase {

    private final AuthUserRepositoryPort repository;
    private final PasswordHasherPort passwordHasher;
    private final EventProducerPort eventProducer;
    private final TokenProviderPort tokenProvider;

    /**
     * Registers a new user using the provided registration request.
     * <p>
     * If the user is successfully registered, a {@link UserRegisteredEvent}
     * is published and an authentication token is returned.
     * </p>
     *
     * @param command the registration request containing email and password
     * @return a {@link RegisterUserResult} containing the authentication token
     * 
     * @throws BusinessException with code {@link AuthBusinessErrorCode#INVALID_REGISTER_COMMAND}
     * if the request is invalid
     * 
     * @throws BusinessException with code {@link AuthBusinessErrorCode#USER_ALREADY_EXISTS} 
     * if a user with the given email already exists
     */
    @Override
    public RegisterUserResult register(RegisterUserCommand command) {
        // Step 1: Validate command
        this.validateCommand(command);

        // Step 2: Check if user already exists
        repository.findByEmail(command.email())
            .ifPresent(user -> {
                throw new BusinessException(
                    AuthBusinessErrorCode.USER_ALREADY_EXISTS,
                    Map.of(
                        "email", command.email()
                    )
                );
            });

        // Step 3: Create and save user
        String hashedPassword = this.passwordHasher.hash(command.password());
        AuthUser user = new AuthUser(UUID.randomUUID(), command.email(), hashedPassword);
        try {
            repository.save(user);
        } catch (InfrastructureException ex) {
            log.warn(
                "Saved into repository error",
                ex
            );

            if (ex.getErrorCode() == AuthInfrasErrorCode.DATABASE_CONSTRAINT_VIOLATION) {
                throw new BusinessException(
                    AuthBusinessErrorCode.USER_ALREADY_EXISTS,
                    Map.of(
                        "email", command.email()
                    )
                );
            }
            throw ex;
        }

        // Step 4: Publish domain event
        UserRegisteredEvent event = new UserRegisteredEvent(user.getId(), user.getEmail());
        eventProducer.produce(event);

        // Step 5: Return token
        DomainTokenSubject subject = new DomainTokenSubject(
            user.getId()
        );

        String token = tokenProvider.issueAccessToken(subject);
        return new RegisterUserResult(token);
    }

    /**
     * Validates the registration request.
     *
     * @param command the registration request to validate
     * @throws BusinessException with code {@link AuthBusinessErrorCode#INVALID_REGISTER_COMMAND}
     * if the request is null or contains invalid data
     */
    // TODO: Tách lại trách nhiệm validation
    private void validateCommand(RegisterUserCommand command) {
        if (command == null) {
            throw new BusinessException(
                AuthBusinessErrorCode.INVALID_REGISTER_COMMAND
            );
        }
        if (command.email() == null || command.email().isBlank()) {
            throw new BusinessException(
                AuthBusinessErrorCode.INVALID_REGISTER_COMMAND
            );
        }
        if (command.password() == null || command.password().length() < 1) {
            throw new BusinessException(
                AuthBusinessErrorCode.INVALID_REGISTER_COMMAND
            );
        }
    }
}
