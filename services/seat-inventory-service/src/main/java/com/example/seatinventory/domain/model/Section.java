package com.example.seatinventory.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Section(UUID id, String code, String title, int capacity, int reservedSeats) {

    private static final int MAX_CODE_LENGTH = 32;
    private static final int MAX_TITLE_LENGTH = 200;

    public Section {
        Objects.requireNonNull(id, "id");
        requireText(code, "code", MAX_CODE_LENGTH);
        requireText(title, "title", MAX_TITLE_LENGTH);
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        if (reservedSeats < 0 || reservedSeats > capacity) {
            throw new IllegalArgumentException(
                "reservedSeats must be within capacity: " + reservedSeats + " of " + capacity);
        }
    }

    public static Section open(UUID id, String code, String title, int capacity) {
        return new Section(id, code, title, capacity, 0);
    }

    public int seatsLeft() {
        return capacity - reservedSeats;
    }

    private static void requireText(String value, String name, int maxLength) {
        Objects.requireNonNull(value, name);
        if (value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(name + " must have 1 to " + maxLength + " characters: " + value);
        }
    }
}
