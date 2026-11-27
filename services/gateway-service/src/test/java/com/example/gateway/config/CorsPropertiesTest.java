package com.example.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CorsPropertiesTest {

    @Test
    void keepsOriginsWithSchemeHostAndPort() {
        List<String> origins = List.of("http://localhost:3000", "https://school.example.test");

        CorsProperties properties = new CorsProperties(origins);

        assertThat(properties.allowedOrigins()).containsExactlyElementsOf(origins);
    }

    @ParameterizedTest(name = "origin \"{0}\"")
    @ValueSource(strings = {
        "*",
        "https://*.example.test",
        "https://school.example.test/",
        "https://school.example.test/register",
        "school.example.test",
        "ftp://school.example.test",
        ""
    })
    void rejectsValueThatIsNotAnOrigin(String origin) {
        List<String> origins = List.of(origin);

        assertThatThrownBy(() -> new CorsProperties(origins))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("'" + origin + "'");
    }

    @Test
    void rejectsEmptyListOfOrigins() {
        List<String> origins = List.of();

        assertThatThrownBy(() -> new CorsProperties(origins))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("CORS_ALLOWED_ORIGINS");
    }
}
