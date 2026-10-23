package com.example.auth.infrastructure.security.jwt.rsa;

import java.util.Map;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.example.auth.infrastructure.security.jwt.JwtSigner;
import com.example.auth.infrastructure.security.jwt.key.KeyProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RsaJwtSigner implements JwtSigner {

    private final JwtEncoder encoder;
    private final KeyProvider keyProvider;

    @Override
    public String sign(Map<String, Object> claims) {

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .claims(c -> c.putAll(claims))
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .keyId(keyProvider.currentKeyId())
                .build();

        String token = encoder
                .encode(JwtEncoderParameters.from(header, claimsSet))
                .getTokenValue();

        return token;
    }
}

