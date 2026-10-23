package com.example.user.application.port.in;

import com.example.user.application.dto.UpdateUserProfileCommand;
import com.example.user.application.dto.UserProfileResult;

public interface UpdateUserProfileUseCase {
    public UserProfileResult update(UpdateUserProfileCommand command);
}
