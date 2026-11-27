package com.example.auth.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.io.ClassPathResource;

// Outside the local profile, the database and its credentials come from the environment only, so a
// variable left unset never falls back to a value written in the repository.
class DatabaseCredentialsIT {

    @ParameterizedTest(name = "profile {0}")
    @ValueSource(strings = {"dev", "prod"})
    void takesDatabaseAndCredentialsFromEnvironment(String profile) throws IOException {
        Map<String, Object> variables = Map.of("DB_NAME", "auth_db", "DB_USER", "auth_user", "DB_PASS", "auth_pass");

        PropertyResolver settings = settingsOf(profile, variables);

        assertThat(settings.getProperty("spring.datasource.url")).endsWith("/auth_db");
        assertThat(settings.getProperty("spring.datasource.username")).isEqualTo("auth_user");
        assertThat(settings.getProperty("spring.datasource.password")).isEqualTo("auth_pass");
    }

    @ParameterizedTest(name = "profile {0}, {1}")
    @CsvSource({
        "dev, spring.datasource.url, DB_NAME",
        "dev, spring.datasource.username, DB_USER",
        "dev, spring.datasource.password, DB_PASS",
        "prod, spring.datasource.url, DB_NAME",
        "prod, spring.datasource.username, DB_USER",
        "prod, spring.datasource.password, DB_PASS"
    })
    void hasNoDefaultForUnsetVariable(String profile, String key, String variable) throws IOException {
        PropertyResolver settings = settingsOf(profile, Map.of());

        assertThatThrownBy(() -> settings.getProperty(key))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("'" + variable + "'");
    }

    // Only the profile file and the given variables, never the variables of the machine running the test.
    private static PropertyResolver settingsOf(String profile, Map<String, Object> variables) throws IOException {
        MutablePropertySources sources = new MutablePropertySources();
        sources.addFirst(new MapPropertySource("variables", variables));
        new YamlPropertySourceLoader()
            .load(profile, new ClassPathResource("application-" + profile + ".yml"))
            .forEach(sources::addLast);
        return new PropertySourcesPropertyResolver(sources);
    }
}
