package com.example.auth.infrastructure.security.jwt.rsa;

import java.security.KeyPair;
import org.springframework.stereotype.Component;

import com.example.auth.infrastructure.security.jwt.key.KeyProvider;

@Component
public class RsaKeyProvider implements KeyProvider {

    private static final String KID = "auth-key-1";
    private final KeyPair keyPair;

    public RsaKeyProvider(RsaKeyLoader loader) {
        this.keyPair = loader.load();
    }

	@Override
    public KeyPair current() {
        return this.keyPair;
    }

    @Override
    public String currentKeyId() {
        return KID;
    }
    
}
