package com.example.seatinventory.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SeatHold(
    UUID id, UUID sectionId, UUID studentId, Instant heldAt, Instant expiresAt, SeatHoldStatus status
) {

    public SeatHold {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sectionId, "sectionId");
        Objects.requireNonNull(studentId, "studentId");
        Objects.requireNonNull(heldAt, "heldAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(status, "status");
        if (expiresAt.isBefore(heldAt)) {
            throw new IllegalArgumentException("expiresAt must not be before heldAt: " + expiresAt + " < " + heldAt);
        }
    }

    public boolean isConfirmed() {
        return status == SeatHoldStatus.CONFIRMED;
    }

    public SeatHold confirmed() {
        return new SeatHold(id, sectionId, studentId, heldAt, expiresAt, SeatHoldStatus.CONFIRMED);
    }
}
