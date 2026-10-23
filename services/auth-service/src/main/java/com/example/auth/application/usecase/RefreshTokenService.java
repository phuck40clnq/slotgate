package com.example.auth.application.usecase;

import org.springframework.stereotype.Service;

import com.example.auth.application.dto.result.RefreshTokenResult;
import com.example.auth.application.port.in.RefreshTokenUseCase;
import com.example.auth.application.dto.command.RefreshTokenCommand;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {
    /**
     * Refresh an access token using a valid refresh token.
     * 
     * Future improvements:
     * - Validate the refresh token (check signature or DB)
     * - Check if token is revoked or expired
     * - Issue a new access token (JWT) without requiring password
     * - Optionally rotate the refresh token for extra security
     * - Emit an event like RefreshTokenUsedEvent for EDA / auditing
     */
    @Override
    public RefreshTokenResult refresh(RefreshTokenCommand command) {
        // Step 1: Validate refresh token (currently stub)
        // e.g., command.getRefreshToken() != null

        // Step 2: Lookup refresh token in DB or cache (future)
        // Step 3: Revoke old refresh token if using rotation (future)

        // Step 4: Issue new access token
        // Currently we just return a fake token for learning purposes
        return new RefreshTokenResult("fake-refreshed-token");
    }

}
