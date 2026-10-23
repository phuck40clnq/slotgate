package com.example.auth.infrastructure.security.jwt.jwks;

import java.util.List;

public interface JwksProvider {
    List<KeyDescriptor> getKeys();
}
