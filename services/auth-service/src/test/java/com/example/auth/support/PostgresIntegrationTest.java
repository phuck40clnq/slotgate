package com.example.auth.support;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * Runs the whole service against a real PostgreSQL server that the test starts itself (ADR-0004), on the schema
 * that the Flyway migrations create, with signing keys made for the test run.
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class PostgresIntegrationTest {

    private static final EmbeddedPostgres POSTGRES = startPostgres();
    private static final SigningKeyFiles SIGNING_KEYS = SigningKeyFiles.generate();

    @DynamicPropertySource
    static void datasourceAndSigningKeys(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
        registry.add("app.security.jwt.private-key", SIGNING_KEYS::privateKeyLocation);
        registry.add("app.security.jwt.public-key", SIGNING_KEYS::publicKeyLocation);
    }

    private static EmbeddedPostgres startPostgres() {
        try {
            return EmbeddedPostgres.builder().start();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
