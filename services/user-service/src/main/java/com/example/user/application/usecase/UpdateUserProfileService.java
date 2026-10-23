package com.example.user.application.usecase;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.user.application.dto.UpdateUserProfileCommand;
import com.example.user.application.dto.UserProfileResult;
import com.example.user.application.port.in.UpdateUserProfileUseCase;
import com.example.user.application.port.out.UserRepositoryPort;
import com.example.user.common.error.UserBusinessErrorCode;
import com.example.user.common.exception.BusinessException;
import com.example.user.common.exception.InfrastructureException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateUserProfileService implements UpdateUserProfileUseCase {
    
    private final UserRepositoryPort repository;

    @Override
    public UserProfileResult update(UpdateUserProfileCommand command) {
        var userId = command.userId();
        var user = repository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                    UserBusinessErrorCode.USER_NOT_FOUND,
                    Map.of(
                        "userId", userId 
                    )
                ));

        user.updateFullName(command.fullName());
        user.updateAvatarUrl(command.avatarUrl());

        try {
            repository.save(user);
            return UserProfileResult.of(user);
        } catch (InfrastructureException ex) {
            log.warn(
                "Saved into repository error",
                ex
            );
            throw ex;
        }
    }

}
