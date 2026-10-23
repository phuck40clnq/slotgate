package com.example.auth.infrastructure.security.jwt.rsa;

import java.security.KeyPair;

public interface RsaKeyLoader {
    KeyPair load();
}
