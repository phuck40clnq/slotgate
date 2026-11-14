package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.InMemorySeatHolds;
import com.example.seatinventory.support.InMemorySeatInventory;

class ExpireSeatHoldsServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-14T12:00:00Z");
    private static final UUID OTHER_SECTION_ID = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final InMemorySeatHolds seatHolds = new InMemorySeatHolds(seatInventory);
    private final ExpireSeatHoldsService service = new ExpireSeatHoldsService(
        seatHolds, seatInventory, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void givesTheSeatsOfExpiredHoldsBack() {
        seatInventory.save(new Section(SECTION_ID, 40, 3));
        SeatHold firstExpired = hold(SECTION_ID, NOW.minusSeconds(1), SeatHoldStatus.HELD);
        SeatHold secondExpired = hold(SECTION_ID, NOW, SeatHoldStatus.HELD);
        SeatHold stillHeld = hold(SECTION_ID, NOW.plusSeconds(1), SeatHoldStatus.HELD);
        seatHolds.add(firstExpired);
        seatHolds.add(secondExpired);
        seatHolds.add(stillHeld);

        service.expireHoldsIn(SECTION_ID);

        assertThat(statusOf(firstExpired)).isEqualTo(SeatHoldStatus.EXPIRED);
        assertThat(statusOf(secondExpired)).isEqualTo(SeatHoldStatus.EXPIRED);
        assertThat(statusOf(stillHeld)).isEqualTo(SeatHoldStatus.HELD);
        assertThat(seatInventory.findSection(SECTION_ID)).get().returns(1, Section::reservedSeats);
    }

    @Test
    void keepsConfirmedHoldsAndTheirSeats() {
        seatInventory.save(new Section(SECTION_ID, 40, 1));
        SeatHold confirmed = hold(SECTION_ID, NOW.minusSeconds(1), SeatHoldStatus.CONFIRMED);
        seatHolds.add(confirmed);

        service.expireHoldsIn(SECTION_ID);

        assertThat(statusOf(confirmed)).isEqualTo(SeatHoldStatus.CONFIRMED);
        assertThat(seatInventory.findSection(SECTION_ID)).get().returns(1, Section::reservedSeats);
    }

    @Test
    void findsOnlySectionsWithExpiredHolds() {
        seatHolds.add(hold(SECTION_ID, NOW.minusSeconds(1), SeatHoldStatus.HELD));
        seatHolds.add(hold(SECTION_ID, NOW.minusSeconds(2), SeatHoldStatus.HELD));
        seatHolds.add(hold(OTHER_SECTION_ID, NOW.plusSeconds(1), SeatHoldStatus.HELD));

        assertThat(service.findSectionsWithExpiredHolds()).containsExactly(SECTION_ID);
    }

    private SeatHold hold(UUID sectionId, Instant expiresAt, SeatHoldStatus status) {
        return new SeatHold(UUID.randomUUID(), sectionId, UUID.randomUUID(), expiresAt.minus(Duration.ofMinutes(5)),
            expiresAt, status);
    }

    private SeatHoldStatus statusOf(SeatHold seatHold) {
        return seatHolds.findHold(seatHold.id()).orElseThrow().status();
    }
}
