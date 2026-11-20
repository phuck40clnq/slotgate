package com.example.gateway.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

// Stands in for auth-service: it signs tokens and serves the key set the gateway verifies them with.
public final class TokenIssuer {

    // The audience that application.yml requires.
    private static final String AUDIENCE = "gateway";
    // A fixed expiry keeps tokens valid without reading the system clock.
    private static final Instant EXPIRES_AT = Instant.parse("2100-01-01T00:00:00Z");
    private static final String KEY_SET_PATH = "/oauth2/jwks";

    private final RSAKey key;
    private final HttpServer server;

    private TokenIssuer(RSAKey key, HttpServer server) {
        this.key = Objects.requireNonNull(key, "key");
        this.server = Objects.requireNonNull(server, "server");
    }

    /** Generates a signing key and serves its public half on a free port. */
    public static TokenIssuer start() {
        try {
            RSAKey key = new RSAKeyGenerator(2048).keyID("test-key").generate();
            byte[] keySet = new JWKSet(key.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext(KEY_SET_PATH, exchange -> serve(exchange, keySet));
            server.start();
            return new TokenIssuer(key, server);
        } catch (JOSEException exception) {
            throw new IllegalStateException("Signing key was not generated", exception);
        } catch (IOException exception) {
            throw new UncheckedIOException("Key set server did not start", exception);
        }
    }

    public String keySetUrl() {
        return "http://localhost:" + server.getAddress().getPort() + KEY_SET_PATH;
    }

    /** Signs a token for the subject that the gateway accepts. */
    public String issueFor(String subject) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(subject)
            .audience(AUDIENCE)
            .expirationTime(Date.from(EXPIRES_AT))
            .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build();
        SignedJWT token = new SignedJWT(header, claims);
        try {
            token.sign(new RSASSASigner(key));
            return token.serialize();
        } catch (JOSEException exception) {
            throw new IllegalStateException("Token was not signed", exception);
        }
    }

    public void stop() {
        server.stop(0);
    }

    private static void serve(HttpExchange exchange, byte[] keySet) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, keySet.length);
        exchange.getResponseBody().write(keySet);
        exchange.close();
    }
}
