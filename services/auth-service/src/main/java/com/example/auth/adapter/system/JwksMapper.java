package com.example.auth.adapter.system;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.example.auth.adapter.system.dto.JwksResponse;
import com.example.auth.infrastructure.security.jwt.jwks.KeyDescriptor;

public final class JwksMapper {
    
    private JwksMapper() {}

    public static JwksResponse toResponse(List<KeyDescriptor> keys) {
        return new JwksResponse(
            keys.stream()
                .map(JwksMapper::toJwk)
                .toList()
        );
    }

    private static Map<String, Object> toJwk(KeyDescriptor key) {
        RSAPublicKey rsa = (RSAPublicKey) key.publicKey();

        return Map.of(
            "kty", "RSA",
            "kid", key.keyId(),
            "use", key.usage(),     // "sig"
            "alg", key.algorithm(), // "RS256"
            "n", base64Url(rsa.getModulus().toByteArray()),
            "e", base64Url(rsa.getPublicExponent().toByteArray())
        );
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }

}
