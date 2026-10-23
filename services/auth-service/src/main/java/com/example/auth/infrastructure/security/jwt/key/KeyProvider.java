package com.example.auth.infrastructure.security.jwt.key;

import java.security.KeyPair;

public interface KeyProvider {
    
    /** Key for SIGN new token */
    KeyPair current();

    /** Key id (kid) current */
    String currentKeyId();

}
