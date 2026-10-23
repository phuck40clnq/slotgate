package com.example.auth.infrastructure.security.jwt.rsa;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.auth.infrastructure.security.jwt.jwks.JwksProvider;
import com.example.auth.infrastructure.security.jwt.jwks.KeyDescriptor;
import com.example.auth.infrastructure.security.jwt.key.KeyProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RsaJwksProvider implements JwksProvider {

    private final KeyProvider keyProvider;

    @Override
    public List<KeyDescriptor> getKeys() {
        return List.of(
            new KeyDescriptor(
                keyProvider.currentKeyId(),
                "RS256",
                "sig",
                keyProvider.current().getPublic()
            )
        );
    }
    
}
