package com.example.seatinventory.domain.model;

public sealed interface SeatClaim {

    record Claimed() implements SeatClaim {
    }

    record SectionFull() implements SeatClaim {
    }

    record SectionNotFound() implements SeatClaim {
    }
}
