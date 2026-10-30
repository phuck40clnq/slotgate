package com.example.seatinventory.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SeatHold(UUID id, UUID sectionId, UUID studentId, Instant heldAt) {

    public SeatHold {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sectionId, "sectionId");
        Objects.requireNonNull(studentId, "studentId");
        Objects.requireNonNull(heldAt, "heldAt");
    }
}
