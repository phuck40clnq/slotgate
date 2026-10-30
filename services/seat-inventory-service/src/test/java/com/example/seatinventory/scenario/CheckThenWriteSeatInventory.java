package com.example.seatinventory.scenario;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

/**
 * The obvious but wrong claim: the seat count is read, checked and written in separate statements, so
 * concurrent callers act on the same stale count. Exists only to reproduce {@link OversellScenario}.
 */
class CheckThenWriteSeatInventory implements SeatInventoryPort {

    private final SeatInventoryPort seatInventory;
    private final JdbcClient jdbcClient;

    CheckThenWriteSeatInventory(SeatInventoryPort seatInventory, JdbcClient jdbcClient) {
        this.seatInventory = seatInventory;
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(Section section) {
        seatInventory.save(section);
    }

    @Override
    public Optional<Section> findSection(UUID sectionId) {
        return seatInventory.findSection(sectionId);
    }

    @Override
    public SeatClaim claimSeat(UUID sectionId) {
        Section section = findSection(sectionId).orElseThrow();
        if (section.seatsLeft() == 0) {
            return new SeatClaim.SectionFull();
        }
        jdbcClient.sql("UPDATE section SET reserved_seats = :reservedSeats WHERE id = :id")
            .param("reservedSeats", section.reservedSeats() + 1)
            .param("id", sectionId)
            .update();
        return new SeatClaim.Claimed();
    }
}
