package com.example.seatinventory.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

/**
 * Stores sections and hands out their seats.
 */
public interface SeatInventoryPort {

    /**
     * Saves the section unless another section has its code. Atomic: of concurrent saves with one code, at most
     * one succeeds.
     *
     * @return whether the section was saved
     */
    boolean saveIfCodeIsFree(Section section);

    Optional<Section> findSection(UUID sectionId);

    List<Section> listSections();

    /**
     * Finds the given sections, ordered by code. Unknown ids are left out.
     */
    List<Section> findSections(Set<UUID> sectionIds);

    /**
     * Claims one seat of a section. Atomic: concurrent callers never claim more seats than the capacity.
     */
    SeatClaim claimSeat(UUID sectionId);

    /**
     * Gives seats back to a section. The database refuses to reserve fewer than zero seats.
     */
    void releaseSeats(UUID sectionId, int seats);
}
