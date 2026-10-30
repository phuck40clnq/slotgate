package com.example.seatinventory.support;

import java.util.ArrayList;
import java.util.List;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;

public class InMemorySeatHolds implements SeatHoldRepositoryPort {

    private final List<SeatHold> seatHolds = new ArrayList<>();

    @Override
    public void save(SeatHold seatHold) {
        seatHolds.add(seatHold);
    }

    public List<SeatHold> all() {
        return List.copyOf(seatHolds);
    }
}
