package com.example.seatinventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.PostgresIntegrationTest;

/**
 * Runs the migrations over rows that an earlier version stored, which the empty database of the other tests
 * never does. Uses a schema of its own, so the tables of the other tests stay untouched.
 */
@RequiredArgsConstructor
class MigrationIT extends PostgresIntegrationTest {

    private static final String SCHEMA = "migration_test";
    private static final UUID OLD_SECTION_ID = UUID.fromString("00000000-0000-0000-0000-0000000000a1");

    private final DataSource dataSource;

    private final JdbcClient jdbcClient;

    @Test
    void givesSectionsOpenedBeforeCodesExistedACodeTheServiceCanRead() {
        migrateTo("3");
        jdbcClient.sql("INSERT INTO " + SCHEMA + ".section (id, capacity, reserved_seats) VALUES (:id, 5, 1)")
            .param("id", OLD_SECTION_ID)
            .update();

        migrateTo("latest");

        assertThat(sectionsIn()).singleElement()
            .returns(OLD_SECTION_ID, Section::id)
            .returns(1, Section::reservedSeats);
    }

    private void migrateTo(String version) {
        Flyway.configure()
            .dataSource(dataSource)
            .schemas(SCHEMA)
            .target(version)
            .load()
            .migrate();
    }

    private List<Section> sectionsIn() {
        return jdbcClient.sql("SELECT id, code, title, capacity, reserved_seats FROM " + SCHEMA + ".section")
            .query((row, rowNumber) -> new Section(
                row.getObject("id", UUID.class),
                row.getString("code"),
                row.getString("title"),
                row.getInt("capacity"),
                row.getInt("reserved_seats")))
            .list();
    }
}
