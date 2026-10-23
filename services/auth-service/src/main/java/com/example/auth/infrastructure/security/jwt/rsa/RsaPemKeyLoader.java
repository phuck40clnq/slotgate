package com.example.auth.infrastructure.security.jwt.rsa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.example.auth.infrastructure.security.jwt.JwtProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// @ConditionalOnProperty(
//     name = "app.security.jwt.rsa.key-source",
//     havingValue = "pem"
// )
public class RsaPemKeyLoader implements RsaKeyLoader {
    
    private static final String ALGORITHM   = "RSA";
    private static final String PRIVATE_KEY = "PRIVATE KEY";
    private static final String PUBLIC_KEY  = "PUBLIC KEY";
    
    private final JwtProperties props;

	@Override
	public KeyPair load() {
        try {
            PrivateKey privateKey = loadPrivateKey(props.getPrivateKey());
            PublicKey publicKey = loadPublicKey(props.getPublicKey());
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA key pair", e);
        }
    }

    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        byte[] pkcs8DerPrivateKey = this.decodePem(resource, PRIVATE_KEY);
        PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(pkcs8DerPrivateKey));
        
        if (!(key instanceof RSAPrivateKey rsaKey)) {
            throw new IllegalArgumentException("Private key is not RSA");
        }
        return rsaKey;
    }

    private PublicKey loadPublicKey(Resource resource) throws Exception {
        byte[] x509DerPublicKey = this.decodePem(resource, PUBLIC_KEY);

        PublicKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(x509DerPublicKey));

        if (!(key instanceof RSAPublicKey rsaKey)) {
            throw new IllegalArgumentException("Public key is not RSA");
        }

        return rsaKey;
    }

    private byte[] decodePem(Resource resource, String type) throws Exception {
        String pem = this.readResource(resource);
        return Base64.getDecoder()
                .decode(this.stripPem(pem, type));
    }

    private String readResource(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String stripPem(String pem, String type) {
        return pem
                .replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "")
                .replaceAll("\\s", "");
    }

}
