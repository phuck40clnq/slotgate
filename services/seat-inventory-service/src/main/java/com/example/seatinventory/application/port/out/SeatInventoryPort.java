package com.example.seatinventory.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

/**
 * Stores sections and hands out their seats.
 */
public interface SeatInventoryPort {

    void save(Section section);

    Optional<Section> findSection(UUID sectionId);

    /**
     * Claims one seat of a section. Atomic: concurrent callers never claim more seats than the capacity.
     */
    SeatClaim claimSeat(UUID sectionId);
}
