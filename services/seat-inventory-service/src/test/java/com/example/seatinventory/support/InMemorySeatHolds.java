package com.example.seatinventory.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;

public class InMemorySeatHolds implements SeatHoldRepositoryPort {

    private final InMemorySeatInventory seatInventory;
    private final List<SeatHold> seatHolds = new ArrayList<>();

    public InMemorySeatHolds(InMemorySeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }

    @Override
    public boolean saveIfAbsent(SeatHold seatHold) {
        boolean isSectionKnown = seatInventory.findSection(seatHold.sectionId()).isPresent();
        boolean isAlreadyHeld = findHold(seatHold.sectionId(), seatHold.studentId()).isPresent();
        if (!isSectionKnown || isAlreadyHeld) {
            return false;
        }
        seatHolds.add(seatHold);
        return true;
    }

    @Override
    public Optional<SeatHold> findHold(UUID sectionId, UUID studentId) {
        return seatHolds.stream()
            .filter(seatHold -> seatHold.sectionId().equals(sectionId) && seatHold.studentId().equals(studentId))
            .findFirst();
    }

    public List<SeatHold> all() {
        return List.copyOf(seatHolds);
    }
}
