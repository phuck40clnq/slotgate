package com.example.seatinventory.support;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;

public class InMemorySeatHolds implements SeatHoldRepositoryPort {

    private final InMemorySeatInventory seatInventory;
    private final Map<UUID, SeatHold> seatHolds = new LinkedHashMap<>();

    public InMemorySeatHolds(InMemorySeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    @Override
    public boolean saveIfAbsent(SeatHold seatHold) {
        boolean isSectionKnown = seatInventory.findSection(seatHold.sectionId()).isPresent();
        boolean hasActiveHold = findActiveHold(seatHold.sectionId(), seatHold.studentId()).isPresent();
        if (!isSectionKnown || hasActiveHold) {
            return false;
        }
        add(seatHold);
        return true;
    }

    @Override
    public Optional<SeatHold> findActiveHold(UUID sectionId, UUID studentId) {
        return seatHolds.values().stream()
            .filter(seatHold -> seatHold.sectionId().equals(sectionId) && seatHold.studentId().equals(studentId))
            .filter(seatHold -> seatHold.status() != SeatHoldStatus.EXPIRED)
            .findFirst();
    }

    @Override
    public Optional<SeatHold> findHold(UUID holdId) {
        return Optional.ofNullable(seatHolds.get(holdId));
    }

    @Override
    public boolean confirm(UUID holdId, Instant now) {
        SeatHold seatHold = seatHolds.get(holdId);
        boolean isConfirmable = seatHold != null
            && seatHold.status() == SeatHoldStatus.HELD
            && seatHold.expiresAt().isAfter(now);
        if (!isConfirmable) {
            return false;
        }
        seatHolds.put(holdId, seatHold.confirmed());
        return true;
    }

    public void add(SeatHold seatHold) {
        seatHolds.put(seatHold.id(), seatHold);
    }

    public List<SeatHold> all() {
        return List.copyOf(seatHolds.values());
    }
}
