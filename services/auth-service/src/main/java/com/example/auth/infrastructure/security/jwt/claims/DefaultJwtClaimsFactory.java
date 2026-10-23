package com.example.auth.infrastructure.security.jwt.claims;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.auth.domain.security.DomainTokenSubject;
import com.example.auth.infrastructure.security.jwt.JwtProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultJwtClaimsFactory
        implements JwtClaimsFactory {
    
    private final JwtProperties props;

	@Override
	public Map<String, Object> createAccessTokenClaims(DomainTokenSubject user) {
        Instant now = Instant.now();

        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtClaimNames.ISSUER, props.getIssuer());

        claims.put(JwtClaimNames.SUBJECT, user.userId().toString());
        claims.put(JwtClaimNames.AUDIENCE, props.getAudiences());
        claims.put(JwtClaimNames.ISSUED_AT, now.getEpochSecond());
        claims.put(JwtClaimNames.EXPIRES_AT,
                   now.plusSeconds(props.getAccessTokenExp()).getEpochSecond());

        return claims;
    }
}
