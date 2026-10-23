package com.example.user.application.usecase;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.user.application.dto.UserProfileResult;
import com.example.user.application.port.in.GetUserProfileUseCase;
import com.example.user.application.port.out.UserRepositoryPort;
import com.example.user.common.error.UserBusinessErrorCode;
import com.example.user.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetUserProfileService implements GetUserProfileUseCase {
    
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserProfileResult get(UUID userId) {
        var user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new BusinessException(
                    UserBusinessErrorCode.USER_NOT_FOUND,
                    Map.of(
                        "userId", userId 
                    )
                ));
        return UserProfileResult.of(user);
    }

}
