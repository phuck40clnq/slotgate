package com.example.seatinventory.application.port.in;

import java.util.List;
import java.util.UUID;

/**
 * Ends the holds that were not confirmed within their hold period and gives their seats back.
 * One call of {@link #expireHoldsIn} covers one section, so that it locks one section row at a time.
 */
public interface ExpireSeatHoldsUseCase {

    List<UUID> findSectionsWithExpiredHolds();

    void expireHoldsIn(UUID sectionId);
}
