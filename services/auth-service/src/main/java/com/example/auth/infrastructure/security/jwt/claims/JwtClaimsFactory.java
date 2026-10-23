package com.example.auth.infrastructure.security.jwt.claims;

import java.util.Map;

import com.example.auth.domain.security.DomainTokenSubject;

public interface JwtClaimsFactory {
    Map<String, Object> createAccessTokenClaims(DomainTokenSubject user);
}
