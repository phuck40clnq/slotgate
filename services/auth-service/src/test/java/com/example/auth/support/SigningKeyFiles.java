package com.example.auth.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

// Writes a fresh RSA key pair as PEM files for one test run, so no signing key lives in the repository.
public final class SigningKeyFiles {

    private static final int KEY_SIZE_BITS = 2048;
    private static final byte[] LINE_SEPARATOR = "\n".getBytes(StandardCharsets.US_ASCII);

    private final Path privateKey;
    private final Path publicKey;

    private SigningKeyFiles(Path privateKey, Path publicKey) {
        this.privateKey = Objects.requireNonNull(privateKey, "privateKey");
        this.publicKey = Objects.requireNonNull(publicKey, "publicKey");
    }

    /** Generates a key pair and writes it to a new temporary folder that is deleted when the JVM exits. */
    public static SigningKeyFiles generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE_BITS);
            KeyPair keyPair = generator.generateKeyPair();
            Path folder = Files.createTempDirectory("auth-signing-keys");
            folder.toFile().deleteOnExit();
            return new SigningKeyFiles(
                write(folder.resolve("private.pem"), "PRIVATE KEY", keyPair.getPrivate().getEncoded()),
                write(folder.resolve("public.pem"), "PUBLIC KEY", keyPair.getPublic().getEncoded()));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("RSA keys cannot be generated", exception);
        } catch (IOException exception) {
            throw new UncheckedIOException("Signing key files were not written", exception);
        }
    }

    public String privateKeyLocation() {
        return privateKey.toUri().toString();
    }

    public String publicKeyLocation() {
        return publicKey.toUri().toString();
    }

    private static Path write(Path file, String type, byte[] encodedKey) throws IOException {
        String body = Base64.getMimeEncoder(64, LINE_SEPARATOR).encodeToString(encodedKey);
        Files.writeString(file, "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----\n");
        file.toFile().deleteOnExit();
        return file;
    }
}
