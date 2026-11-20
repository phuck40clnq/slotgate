package com.example.seatinventory.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.seatinventory.domain.model.SeatHold;

/**
 * Stores the seats held by students. A student has at most one active (held or confirmed) hold per section.
 */
public interface SeatHoldRepositoryPort {

    /**
     * Saves the hold unless the student already has an active hold in the section or the section does not
     * exist. Atomic: concurrent calls for the same student and section save at most one hold.
     *
     * @return whether the hold was saved
     */
    boolean saveIfAbsent(SeatHold seatHold);

    Optional<SeatHold> findActiveHold(UUID sectionId, UUID studentId);

    Optional<SeatHold> findHold(UUID holdId);

    /**
     * Finds the holds of a student that are current at {@code now}: confirmed, or held and before their expiry.
     * A held hold whose time is up is left out even before the sweep expires it, since it can no longer be
     * confirmed.
     */
    List<SeatHold> findCurrentHolds(UUID studentId, Instant now);

    /**
     * Confirms the hold if it is still held and has not expired at {@code now}. Atomic: of concurrent
     * confirmations and expiries of one hold, exactly one changes it.
     *
     * @return whether this call confirmed the hold
     */
    boolean confirm(UUID holdId, Instant now);

    List<UUID> findSectionsWithExpiredHolds(Instant now);

    /**
     * Expires the holds of a section that are still held and whose hold period ended at {@code now}. Atomic
     * per hold against {@link #confirm}: a hold is either confirmed or expired, never both.
     *
     * @return how many holds this call expired
     */
    int expireHolds(UUID sectionId, Instant now);
}
