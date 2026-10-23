package com.example.auth.infrastructure.security.jwt;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.auth.application.port.out.TokenProviderPort;
import com.example.auth.domain.security.DomainTokenSubject;
import com.example.auth.infrastructure.security.jwt.claims.JwtClaimsFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider
        implements TokenProviderPort {

    private final JwtClaimsFactory claimsFactory;
    private final JwtSigner signer;

	@Override
    public String issueAccessToken(DomainTokenSubject user) {
        Map<String, Object> map = claimsFactory.createAccessTokenClaims(user);
        return signer.sign(map);
    }

}
