package com.example.auth.infrastructure.security.jwt.jwks;

import java.security.PublicKey;

public record KeyDescriptor(
    String keyId,
    String algorithm,
    String usage,
    PublicKey publicKey
) {}
