package com.example.seatinventory.adapter.system;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.seatinventory.application.port.in.ExpireSeatHoldsUseCase;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(prefix = "seat-hold", name = "sweep-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SeatHoldExpiryScheduler {

    private final ExpireSeatHoldsUseCase expireSeatHolds;

    /**
     * One transaction per section, so a sweep never keeps the rows of many busy sections locked at once.
     * See ADR-0007.
     */
    @Scheduled(fixedDelayString = "${seat-hold.sweep-interval}")
    void expireSeatHolds() {
        for (UUID sectionId : expireSeatHolds.findSectionsWithExpiredHolds()) {
            expireSeatHolds.expireHoldsIn(sectionId);
        }
    }
}
