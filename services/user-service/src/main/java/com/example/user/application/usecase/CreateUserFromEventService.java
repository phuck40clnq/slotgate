package com.example.user.application.usecase;

import org.springframework.stereotype.Service;

import com.example.user.application.dto.*;
import com.example.user.application.port.in.CreateUserFromEventUseCase;
import com.example.user.application.port.out.UserRepositoryPort;
import com.example.user.common.exception.InfrastructureException;
import com.example.user.domain.model.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateUserFromEventService implements CreateUserFromEventUseCase {

    private final UserRepositoryPort repository;
    
    @Override
    public void create(CreateUserCommand command) {
        UserProfile user = new UserProfile(
            command.userId(), command.email(), command.fullName()
        );
        
        try {
            repository.save(user);
        } catch (InfrastructureException ex) {
            log.warn(
                "Saved into repository error",
                ex
            );
            throw ex;
        }

        // TODO: Publish UserProfileCreatedEvent
    }

}
