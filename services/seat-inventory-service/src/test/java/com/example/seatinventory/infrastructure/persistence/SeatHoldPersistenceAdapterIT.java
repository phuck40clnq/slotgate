package com.example.seatinventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.PostgresIntegrationTest;

class SeatHoldPersistenceAdapterIT extends PostgresIntegrationTest {

    private static final UUID STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    private static final Instant HELD_AT = Instant.parse("2026-11-06T12:00:00Z");

    @Autowired
    private SeatHoldRepositoryPort seatHolds;

    @Autowired
    private SeatInventoryPort seatInventory;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void savesHoldOfAStudentInAKnownSection() {
        SeatHold seatHold = holdIn(openSection());

        boolean isSaved = seatHolds.saveIfAbsent(seatHold);

        assertThat(isSaved).isTrue();
        assertThat(seatHolds.findHold(seatHold.sectionId(), STUDENT_ID)).contains(seatHold);
    }

    @Test
    void keepsTheFirstHoldWhenTheStudentHoldsAgain() {
        UUID sectionId = openSection();
        SeatHold firstHold = holdIn(sectionId);
        seatHolds.saveIfAbsent(firstHold);

        boolean isSaved = seatHolds.saveIfAbsent(holdIn(sectionId));

        assertThat(isSaved).isFalse();
        assertThat(seatHolds.findHold(sectionId, STUDENT_ID)).contains(firstHold);
    }

    @Test
    void skipsHoldInAnUnknownSection() {
        UUID unknownSectionId = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

        boolean isSaved = seatHolds.saveIfAbsent(holdIn(unknownSectionId));

        assertThat(isSaved).isFalse();
        assertThat(seatHolds.findHold(unknownSectionId, STUDENT_ID)).isEmpty();
    }

    @Test
    void databaseRefusesASecondHoldOfTheSameStudentInASection() {
        UUID sectionId = openSection();
        seatHolds.saveIfAbsent(holdIn(sectionId));

        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> jdbcClient
            .sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at)
                VALUES (:id, :sectionId, :studentId, :heldAt)
                """)
            .param("id", UUID.randomUUID())
            .param("sectionId", sectionId)
            .param("studentId", STUDENT_ID)
            .param("heldAt", OffsetDateTime.ofInstant(HELD_AT, ZoneOffset.UTC))
            .update());
    }

    private UUID openSection() {
        Section section = Section.open(UUID.randomUUID(), 10);
        seatInventory.save(section);
        return section.id();
    }

    private SeatHold holdIn(UUID sectionId) {
        return new SeatHold(UUID.randomUUID(), sectionId, STUDENT_ID, HELD_AT);
    }
}
