package com.example.seatinventory.application.usecase;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.seatinventory.application.port.in.ExpireSeatHoldsUseCase;
import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.application.port.out.SeatInventoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpireSeatHoldsService implements ExpireSeatHoldsUseCase {

    private final SeatHoldRepositoryPort seatHolds;
    private final SeatInventoryPort seatInventory;
    private final Clock clock;

    @Override
    public List<UUID> findSectionsWithExpiredHolds() {
        return seatHolds.findSectionsWithExpiredHolds(clock.instant());
    }

    /**
     * Expiring the holds and giving their seats back commit together, so the seat count always matches the
     * held and confirmed holds. Hold rows are locked before the section row, in the same order as a new hold,
     * so the two cannot deadlock. See ADR-0007.
     */
    @Override
    @Transactional
    public void expireHoldsIn(UUID sectionId) {
        int expiredHolds = seatHolds.expireHolds(sectionId, clock.instant());
        if (expiredHolds == 0) {
            return;
        }
        seatInventory.releaseSeats(sectionId, expiredHolds);
        log.debug("Seat holds expired: sectionId={}, expiredHolds={}", sectionId, expiredHolds);
    }
}
