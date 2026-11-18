package com.example.seatinventory.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.ExpireSeatHoldsUseCase;
import com.example.seatinventory.application.port.in.GetSectionUseCase;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.in.ReserveSeatUseCase;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.support.PostgresIntegrationTest;
import com.example.seatinventory.support.SectionFixtures;

/**
 * PRD failure scenario "phantom holds": students hold every seat of a section and never confirm. Without a
 * sweep, as before holds expired, a late student is turned away for good; with one, the seats come back soon
 * after the holds expire. Not part of the build. Run it on demand: {@code ./mvnw verify -Dit.test=PhantomHoldScenario}
 */
@TestPropertySource(properties = "seat-hold.ttl=PT1S")
class PhantomHoldScenario extends PostgresIntegrationTest {

    private static final int SEATS = 10;
    private static final Duration SWEEP_INTERVAL = Duration.ofMillis(500);
    private static final Duration PATIENCE = Duration.ofSeconds(3);
    private static final Duration RETRY_PAUSE = Duration.ofMillis(20);
    private static final UUID LATE_STUDENT_ID = new UUID(1, 0);

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private GetSectionUseCase getSection;

    @Autowired
    private ReserveSeatUseCase reserveSeat;

    @Autowired
    private ExpireSeatHoldsUseCase expireSeatHolds;

    @Test
    void withoutASweepAbandonedHoldsKeepTheSectionFull() {
        UUID sectionId = openSection.openSection(SectionFixtures.openSectionCommand(SEATS)).sectionId();
        abandonEverySeat(sectionId);

        await().during(PATIENCE).atMost(PATIENCE.plusSeconds(1)).pollInterval(RETRY_PAUSE)
            .until(() -> !holdsSeatForTheLateStudent(sectionId));

        System.out.printf("SCENARIO no sweep: seats=%d holdPeriod=1s lateStudentRejectedFor=%dms seatsLeft=%d%n",
            SEATS, PATIENCE.toMillis(), seatsLeftIn(sectionId));
        assertThat(seatsLeftIn(sectionId)).isZero();
    }

    @Test
    void aSweepGivesTheSeatsBackSoonAfterTheHoldsExpire() {
        UUID sectionId = openSection.openSection(SectionFixtures.openSectionCommand(SEATS)).sectionId();
        List<Instant> expiries = abandonEverySeat(sectionId);

        SeatsBack seatsBack = whileSweeping(() -> awaitSeatsBack(sectionId));

        System.out.printf("SCENARIO sweep every %dms: seats=%d holdPeriod=1s firstSeatBackAfter=%dms "
                + "allSeatsBackAfter=%dms seatsLeft=%d%n",
            SWEEP_INTERVAL.toMillis(), SEATS,
            Duration.between(Collections.min(expiries), seatsBack.firstSeatAt()).toMillis(),
            Duration.between(Collections.max(expiries), seatsBack.allSeatsAt()).toMillis(),
            seatsLeftIn(sectionId));
        assertThat(seatsLeftIn(sectionId)).isEqualTo(SEATS - 1);
    }

    private record SeatsBack(Instant firstSeatAt, Instant allSeatsAt) {
    }

    private List<Instant> abandonEverySeat(UUID sectionId) {
        return IntStream.range(0, SEATS)
            .mapToObj(studentIndex -> reserveSeat.reserveSeat(
                new ReserveSeatCommand(sectionId, new UUID(0, studentIndex))).expiresAt())
            .toList();
    }

    private SeatsBack awaitSeatsBack(UUID sectionId) {
        await().atMost(PATIENCE).pollInterval(RETRY_PAUSE).until(() -> holdsSeatForTheLateStudent(sectionId));
        Instant firstSeatAt = Instant.now();
        await().atMost(PATIENCE).pollInterval(RETRY_PAUSE).until(() -> seatsLeftIn(sectionId) == SEATS - 1);
        return new SeatsBack(firstSeatAt, Instant.now());
    }

    private <T> T whileSweeping(Supplier<T> action) {
        try (ScheduledExecutorService sweeper = Executors.newSingleThreadScheduledExecutor()) {
            sweeper.scheduleWithFixedDelay(this::sweep, 0, SWEEP_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
            return action.get();
        }
    }

    /**
     * Does what SeatHoldExpiryScheduler does, which the test profile turns off.
     */
    private void sweep() {
        expireSeatHolds.findSectionsWithExpiredHolds().forEach(expireSeatHolds::expireHoldsIn);
    }

    private boolean holdsSeatForTheLateStudent(UUID sectionId) {
        try {
            reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, LATE_STUDENT_ID));
            return true;
        } catch (BusinessException exception) {
            if (exception.getErrorCode() != SeatInventoryBusinessErrorCode.SECTION_FULL) {
                throw exception;
            }
            return false;
        }
    }

    private int seatsLeftIn(UUID sectionId) {
        return getSection.getSection(sectionId).seatsLeft();
    }
}
