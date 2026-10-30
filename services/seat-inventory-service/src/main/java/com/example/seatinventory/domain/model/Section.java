package com.example.seatinventory.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Section(UUID id, int capacity, int reservedSeats) {

    public Section {
        Objects.requireNonNull(id, "id");
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        if (reservedSeats < 0 || reservedSeats > capacity) {
            throw new IllegalArgumentException(
                "reservedSeats must be within capacity: " + reservedSeats + " of " + capacity);
        }
    }

    public static Section open(UUID id, int capacity) {
        return new Section(id, capacity, 0);
    }

    public int seatsLeft() {
        return capacity - reservedSeats;
    }
}
