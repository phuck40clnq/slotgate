package com.example.auth.infrastructure.security.jwt;

import java.util.Map;

public interface JwtSigner {
    String sign(Map<String, Object> claims);
}
