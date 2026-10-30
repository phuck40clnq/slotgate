package com.example.seatinventory.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatInventoryPersistenceAdapter implements SeatInventoryPort {

    private final JdbcClient jdbcClient;

    @Override
    public void save(Section section) {
        jdbcClient.sql("INSERT INTO section (id, capacity, reserved_seats) VALUES (:id, :capacity, :reservedSeats)")
            .param("id", section.id())
            .param("capacity", section.capacity())
            .param("reservedSeats", section.reservedSeats())
            .update();
    }

    @Override
    public Optional<Section> findSection(UUID sectionId) {
        return jdbcClient.sql("SELECT id, capacity, reserved_seats FROM section WHERE id = :id")
            .param("id", sectionId)
            .query((row, rowNumber) -> toSection(row))
            .optional();
    }

    /**
     * Check and increment are one statement. Concurrent claims queue on the row lock, and PostgreSQL
     * re-evaluates the WHERE clause on the latest committed row once the lock is released, so the last
     * seat cannot be claimed twice. See ADR-0005.
     */
    @Override
    public SeatClaim claimSeat(UUID sectionId) {
        int claimedRows = jdbcClient.sql("""
                UPDATE section
                SET reserved_seats = reserved_seats + 1
                WHERE id = :id AND reserved_seats < capacity
                """)
            .param("id", sectionId)
            .update();
        if (claimedRows == 1) {
            return new SeatClaim.Claimed();
        }
        return findSection(sectionId).isPresent() ? new SeatClaim.SectionFull() : new SeatClaim.SectionNotFound();
    }

    private Section toSection(ResultSet row) throws SQLException {
        return new Section(
            row.getObject("id", UUID.class),
            row.getInt("capacity"),
            row.getInt("reserved_seats"));
    }
}
