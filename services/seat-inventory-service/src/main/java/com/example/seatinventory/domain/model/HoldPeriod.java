package com.example.seatinventory.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * How long a student has to confirm a hold before its seat goes back to the section.
 */
public record HoldPeriod(Duration length) {

    public HoldPeriod {
        Objects.requireNonNull(length, "length");
        if (length.isZero() || length.isNegative()) {
            throw new IllegalArgumentException("length must be positive: " + length);
        }
    }

    public Instant expiryFor(Instant heldAt) {
        return heldAt.plus(length);
    }
}
