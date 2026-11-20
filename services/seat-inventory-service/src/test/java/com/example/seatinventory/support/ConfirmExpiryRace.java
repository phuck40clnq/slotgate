package com.example.seatinventory.support;

import static org.awaitility.Awaitility.await;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.seatinventory.application.dto.ConfirmSeatHoldCommand;
import com.example.seatinventory.application.dto.ReserveSeatCommand;
import com.example.seatinventory.application.port.in.OpenSectionUseCase;
import com.example.seatinventory.application.port.out.IdGeneratorPort;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.application.usecase.ConfirmSeatHoldService;
import com.example.seatinventory.application.usecase.ExpireSeatHoldsService;
import com.example.seatinventory.application.usecase.ReserveSeatService;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.HoldPeriod;

/**
 * Lets one sweep race the confirmations of every hold in a section. Every hold qualifies for both: the
 * confirmations run one second before the hold period ends, the sweep one second after. The sweep starts once
 * half of the confirmations have returned, so that the other half are in flight while it runs; started with
 * the others, a single fast sweep either beat every confirmation or came after all of them.
 */
public abstract class ConfirmExpiryRace extends PostgresIntegrationTest {

    private static final Instant HOLD_PERIOD_ENDS = Instant.parse("2026-11-14T12:00:00Z");
    private static final Duration HOLD_PERIOD = Duration.ofMinutes(5);

    @Autowired
    private OpenSectionUseCase openSection;

    @Autowired
    private SeatInventoryPort seatInventory;

    @Autowired
    private IdGeneratorPort idGenerator;

    @Autowired
    private TransactionTemplate transaction;

    @Autowired
    private JdbcClient jdbcClient;

    /**
     * What the callers were told and what the database holds afterwards. Correct when every hold ended either
     * confirmed or expired, the section reserves exactly the confirmed seats, and every caller was told the
     * outcome that the database kept.
     */
    public record Outcome(
        int holds, int confirmedByCallers, int expiredByCallers, int confirmedHolds, int expiredHolds,
        int reservedSeats
    ) {

        public boolean isCorrect() {
            return confirmedHolds + expiredHolds == holds
                && reservedSeats == confirmedHolds
                && confirmedByCallers == confirmedHolds
                && expiredByCallers == expiredHolds;
        }
    }

    protected Outcome race(SeatHoldRepositoryPort seatHolds, int holds) {
        UUID sectionId = openSection.openSection(SectionFixtures.openSectionCommand(holds)).sectionId();
        List<ConfirmSeatHoldCommand> confirmations = holdEverySeat(seatHolds, sectionId, holds);
        ConfirmSeatHoldService confirmSeatHold =
            new ConfirmSeatHoldService(seatHolds, clockAt(HOLD_PERIOD_ENDS.minusSeconds(1)));
        ExpireSeatHoldsService expireSeatHolds =
            new ExpireSeatHoldsService(seatHolds, seatInventory, clockAt(HOLD_PERIOD_ENDS.plusSeconds(1)));
        AtomicInteger confirmedByCallers = new AtomicInteger();
        AtomicInteger expiredByCallers = new AtomicInteger();

        ParallelCallers.run(holds + 1, callerIndex -> {
            if (callerIndex == holds) {
                await().atMost(Duration.ofSeconds(30))
                    .until(() -> confirmedByCallers.get() + expiredByCallers.get() >= holds / 2);
                transaction.executeWithoutResult(status -> expireSeatHolds.expireHoldsIn(sectionId));
                return;
            }
            if (tryToConfirm(confirmSeatHold, confirmations.get(callerIndex))) {
                confirmedByCallers.incrementAndGet();
            } else {
                expiredByCallers.incrementAndGet();
            }
        });

        return new Outcome(holds, confirmedByCallers.get(), expiredByCallers.get(),
            holdsIn(sectionId, "CONFIRMED"), holdsIn(sectionId, "EXPIRED"), reservedSeatsOf(sectionId));
    }

    private List<ConfirmSeatHoldCommand> holdEverySeat(SeatHoldRepositoryPort seatHolds, UUID sectionId, int holds) {
        ReserveSeatService reserveSeat = new ReserveSeatService(seatInventory, seatHolds, idGenerator,
            clockAt(HOLD_PERIOD_ENDS.minus(HOLD_PERIOD)), new HoldPeriod(HOLD_PERIOD));
        return IntStream.range(0, holds)
            .mapToObj(studentIndex -> new UUID(0, studentIndex))
            .map(studentId -> {
                UUID holdId = transaction.execute(status ->
                    reserveSeat.reserveSeat(new ReserveSeatCommand(sectionId, studentId)).holdId());
                return new ConfirmSeatHoldCommand(sectionId, holdId, studentId);
            })
            .toList();
    }

    private boolean tryToConfirm(ConfirmSeatHoldService confirmSeatHold, ConfirmSeatHoldCommand command) {
        try {
            transaction.executeWithoutResult(status -> confirmSeatHold.confirmSeatHold(command));
            return true;
        } catch (BusinessException exception) {
            if (exception.getErrorCode() != SeatInventoryBusinessErrorCode.HOLD_EXPIRED) {
                throw exception;
            }
            return false;
        }
    }

    private int holdsIn(UUID sectionId, String status) {
        return jdbcClient.sql("SELECT count(*) FROM seat_hold WHERE section_id = :id AND status = :status")
            .param("id", sectionId)
            .param("status", status)
            .query(Integer.class)
            .single();
    }

    private int reservedSeatsOf(UUID sectionId) {
        return jdbcClient.sql("SELECT reserved_seats FROM section WHERE id = :id")
            .param("id", sectionId)
            .query(Integer.class)
            .single();
    }

    private static Clock clockAt(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }
}
