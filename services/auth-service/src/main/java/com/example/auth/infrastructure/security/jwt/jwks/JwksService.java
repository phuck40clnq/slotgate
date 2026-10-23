package com.example.auth.infrastructure.security.jwt.jwks;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwksService {

    private final JwksProvider provider;

    public List<KeyDescriptor> getKeyDescriptors() {
        return provider.getKeys();
    }
    
}
