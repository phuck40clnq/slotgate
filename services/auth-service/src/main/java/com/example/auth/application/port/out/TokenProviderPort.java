package com.example.auth.application.port.out;

import com.example.auth.domain.security.DomainTokenSubject;

public interface TokenProviderPort {
    String issueAccessToken(DomainTokenSubject subject);
}
