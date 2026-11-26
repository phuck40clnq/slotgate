package com.example.auth.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;

import com.example.auth.support.PostgresIntegrationTest;

@RequiredArgsConstructor
class MigrationIT extends PostgresIntegrationTest {

    private final Flyway flyway;

    @Test
    void appliesEveryMigrationAtStartUp() {
        MigrationInfoService migrations = flyway.info();

        assertThat(migrations.pending()).isEmpty();
        assertThat(migrations.applied()).isNotEmpty()
            .extracting(MigrationInfo::getState)
            .containsOnly(MigrationState.SUCCESS);
    }
}
