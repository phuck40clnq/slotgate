package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.STUDENT_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.StudentHoldResult;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.InMemorySeatHolds;
import com.example.seatinventory.support.InMemorySeatInventory;

class ListStudentHoldsServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-19T12:00:00Z");
    private static final Instant HELD_AT = NOW.minus(Duration.ofMinutes(10));
    private static final UUID FIRST_SECTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_SECTION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID FIRST_HOLD_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID SECOND_HOLD_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private static final UUID OTHER_STUDENT_ID = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final InMemorySeatHolds seatHolds = new InMemorySeatHolds(seatInventory);
    private final ListStudentHoldsService service = new ListStudentHoldsService(
        seatHolds, seatInventory, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void listsTheCurrentHoldsOfTheStudentWithTheirSectionByCode() {
        openSections();
        seatHolds.add(hold(FIRST_HOLD_ID, SECOND_SECTION_ID, NOW.plusSeconds(1), SeatHoldStatus.HELD));
        seatHolds.add(hold(SECOND_HOLD_ID, FIRST_SECTION_ID, NOW.minusSeconds(1), SeatHoldStatus.CONFIRMED));

        assertThat(service.listStudentHolds(STUDENT_ID)).containsExactly(
            new StudentHoldResult(SECOND_HOLD_ID, FIRST_SECTION_ID, "SIM101 1", "First course", HELD_AT,
                NOW.minusSeconds(1), "CONFIRMED"),
            new StudentHoldResult(FIRST_HOLD_ID, SECOND_SECTION_ID, "SIM202 1", "Second course", HELD_AT,
                NOW.plusSeconds(1), "HELD"));
    }

    @Test
    void leavesOutHoldsWhoseTimeIsUpEvenBeforeTheSweep() {
        openSections();
        seatHolds.add(hold(FIRST_HOLD_ID, FIRST_SECTION_ID, NOW, SeatHoldStatus.HELD));
        seatHolds.add(hold(SECOND_HOLD_ID, SECOND_SECTION_ID, NOW.minusSeconds(1), SeatHoldStatus.EXPIRED));

        assertThat(service.listStudentHolds(STUDENT_ID)).isEmpty();
    }

    @Test
    void leavesOutTheHoldsOfOtherStudents() {
        openSections();
        seatHolds.add(new SeatHold(FIRST_HOLD_ID, FIRST_SECTION_ID, OTHER_STUDENT_ID, HELD_AT, NOW.plusSeconds(1),
            SeatHoldStatus.HELD));

        assertThat(service.listStudentHolds(STUDENT_ID)).isEmpty();
    }

    @Test
    void listsNothingWhenTheStudentHoldsNoSeat() {
        assertThat(service.listStudentHolds(STUDENT_ID)).isEmpty();
    }

    private void openSections() {
        seatInventory.save(new Section(SECOND_SECTION_ID, "SIM202 1", "Second course", 30, 1));
        seatInventory.save(new Section(FIRST_SECTION_ID, "SIM101 1", "First course", 40, 1));
    }

    private static SeatHold hold(UUID holdId, UUID sectionId, Instant expiresAt, SeatHoldStatus status) {
        return new SeatHold(holdId, sectionId, STUDENT_ID, HELD_AT, expiresAt, status);
    }
}
