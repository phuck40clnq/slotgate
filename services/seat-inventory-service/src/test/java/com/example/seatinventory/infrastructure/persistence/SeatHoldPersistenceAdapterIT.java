package com.example.seatinventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Duration;
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
import com.example.seatinventory.domain.model.SeatHoldStatus;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.PostgresIntegrationTest;
import com.example.seatinventory.support.SectionFixtures;

class SeatHoldPersistenceAdapterIT extends PostgresIntegrationTest {

    private static final UUID STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    private static final Instant HELD_AT = Instant.parse("2026-11-06T12:00:00Z");
    private static final Instant EXPIRES_AT = HELD_AT.plus(Duration.ofMinutes(5));

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
        assertThat(seatHolds.findActiveHold(seatHold.sectionId(), STUDENT_ID)).contains(seatHold);
        assertThat(seatHolds.findHold(seatHold.id())).contains(seatHold);
    }

    @Test
    void keepsTheFirstHoldWhenTheStudentHoldsAgain() {
        UUID sectionId = openSection();
        SeatHold firstHold = holdIn(sectionId);
        seatHolds.saveIfAbsent(firstHold);

        boolean isSaved = seatHolds.saveIfAbsent(holdIn(sectionId));

        assertThat(isSaved).isFalse();
        assertThat(seatHolds.findActiveHold(sectionId, STUDENT_ID)).contains(firstHold);
    }

    @Test
    void savesANewHoldOnceTheEarlierHoldExpired() {
        UUID sectionId = openSection();
        SeatHold earlierHold = holdIn(sectionId);
        seatHolds.saveIfAbsent(earlierHold);
        markExpired(earlierHold.id());
        SeatHold newHold = holdIn(sectionId);

        boolean isSaved = seatHolds.saveIfAbsent(newHold);

        assertThat(isSaved).isTrue();
        assertThat(seatHolds.findActiveHold(sectionId, STUDENT_ID)).contains(newHold);
    }

    @Test
    void skipsHoldInAnUnknownSection() {
        UUID unknownSectionId = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

        boolean isSaved = seatHolds.saveIfAbsent(holdIn(unknownSectionId));

        assertThat(isSaved).isFalse();
        assertThat(seatHolds.findActiveHold(unknownSectionId, STUDENT_ID)).isEmpty();
    }

    @Test
    void databaseRefusesASecondActiveHoldOfTheSameStudentInASection() {
        UUID sectionId = openSection();
        seatHolds.saveIfAbsent(holdIn(sectionId));

        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> insertHeldHold(sectionId));
    }

    @Test
    void confirmsAHeldHoldBeforeItExpires() {
        SeatHold seatHold = holdIn(openSection());
        seatHolds.saveIfAbsent(seatHold);

        boolean isConfirmed = seatHolds.confirm(seatHold.id(), EXPIRES_AT.minusSeconds(1));

        assertThat(isConfirmed).isTrue();
        assertThat(seatHolds.findHold(seatHold.id())).contains(seatHold.confirmed());
    }

    @Test
    void confirmsAHoldOnlyOnce() {
        SeatHold seatHold = holdIn(openSection());
        seatHolds.saveIfAbsent(seatHold);
        seatHolds.confirm(seatHold.id(), HELD_AT);

        boolean isConfirmedAgain = seatHolds.confirm(seatHold.id(), HELD_AT);

        assertThat(isConfirmedAgain).isFalse();
    }

    @Test
    void doesNotConfirmAHoldWhoseTimeIsUp() {
        SeatHold seatHold = holdIn(openSection());
        seatHolds.saveIfAbsent(seatHold);

        boolean isConfirmed = seatHolds.confirm(seatHold.id(), EXPIRES_AT);

        assertThat(isConfirmed).isFalse();
        assertThat(seatHolds.findHold(seatHold.id())).get().returns(SeatHoldStatus.HELD, SeatHold::status);
    }

    @Test
    void doesNotConfirmAnExpiredHold() {
        SeatHold seatHold = holdIn(openSection());
        seatHolds.saveIfAbsent(seatHold);
        markExpired(seatHold.id());

        boolean isConfirmed = seatHolds.confirm(seatHold.id(), HELD_AT);

        assertThat(isConfirmed).isFalse();
    }

    @Test
    void expiresOnlyHeldHoldsWhoseTimeIsUp() {
        UUID sectionId = openSection();
        SeatHold timeIsUp = holdOf(sectionId, EXPIRES_AT);
        SeatHold stillRunning = holdOf(sectionId, EXPIRES_AT.plusSeconds(1));
        SeatHold confirmed = holdOf(sectionId, EXPIRES_AT);
        seatHolds.saveIfAbsent(timeIsUp);
        seatHolds.saveIfAbsent(stillRunning);
        seatHolds.saveIfAbsent(confirmed);
        seatHolds.confirm(confirmed.id(), HELD_AT);

        int expiredHolds = seatHolds.expireHolds(sectionId, EXPIRES_AT);

        assertThat(expiredHolds).isEqualTo(1);
        assertThat(statusOf(timeIsUp)).isEqualTo(SeatHoldStatus.EXPIRED);
        assertThat(statusOf(stillRunning)).isEqualTo(SeatHoldStatus.HELD);
        assertThat(statusOf(confirmed)).isEqualTo(SeatHoldStatus.CONFIRMED);
    }

    @Test
    void findsSectionsWithExpiredHolds() {
        UUID sectionWithExpiredHold = openSection();
        UUID sectionWithRunningHold = openSection();
        seatHolds.saveIfAbsent(holdOf(sectionWithExpiredHold, EXPIRES_AT));
        seatHolds.saveIfAbsent(holdOf(sectionWithRunningHold, EXPIRES_AT.plusSeconds(1)));

        assertThat(seatHolds.findSectionsWithExpiredHolds(EXPIRES_AT))
            .contains(sectionWithExpiredHold)
            .doesNotContain(sectionWithRunningHold);
    }

    private SeatHold holdOf(UUID sectionId, Instant expiresAt) {
        return new SeatHold(UUID.randomUUID(), sectionId, UUID.randomUUID(), HELD_AT, expiresAt, SeatHoldStatus.HELD);
    }

    private SeatHoldStatus statusOf(SeatHold seatHold) {
        return seatHolds.findHold(seatHold.id()).orElseThrow().status();
    }

    private UUID openSection() {
        Section section = SectionFixtures.section(UUID.randomUUID(), 10, 0);
        seatInventory.saveIfCodeIsFree(section);
        return section.id();
    }

    private SeatHold holdIn(UUID sectionId) {
        return new SeatHold(UUID.randomUUID(), sectionId, STUDENT_ID, HELD_AT, EXPIRES_AT, SeatHoldStatus.HELD);
    }

    private void markExpired(UUID holdId) {
        jdbcClient.sql("UPDATE seat_hold SET status = 'EXPIRED' WHERE id = :id")
            .param("id", holdId)
            .update();
    }

    private void insertHeldHold(UUID sectionId) {
        jdbcClient.sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at, expires_at, status)
                VALUES (:id, :sectionId, :studentId, :heldAt, :expiresAt, 'HELD')
                """)
            .param("id", UUID.randomUUID())
            .param("sectionId", sectionId)
            .param("studentId", STUDENT_ID)
            .param("heldAt", OffsetDateTime.ofInstant(HELD_AT, ZoneOffset.UTC))
            .param("expiresAt", OffsetDateTime.ofInstant(EXPIRES_AT, ZoneOffset.UTC))
            .update();
    }
}
