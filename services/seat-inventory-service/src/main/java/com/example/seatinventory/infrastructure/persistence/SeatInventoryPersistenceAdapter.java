package com.example.seatinventory.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    /**
     * A concurrent insert with the same code waits on the unique index until the first one commits or rolls
     * back, then either skips or inserts.
     */
    @Override
    public boolean saveIfCodeIsFree(Section section) {
        int savedRows = jdbcClient.sql("""
                INSERT INTO section (id, code, title, capacity, reserved_seats)
                VALUES (:id, :code, :title, :capacity, :reservedSeats)
                ON CONFLICT (code) DO NOTHING
                """)
            .param("id", section.id())
            .param("code", section.code())
            .param("title", section.title())
            .param("capacity", section.capacity())
            .param("reservedSeats", section.reservedSeats())
            .update();
        return savedRows == 1;
    }

    @Override
    public Optional<Section> findSection(UUID sectionId) {
        return jdbcClient.sql("SELECT id, code, title, capacity, reserved_seats FROM section WHERE id = :id")
            .param("id", sectionId)
            .query((row, rowNumber) -> toSection(row))
            .optional();
    }

    @Override
    public List<Section> listSections() {
        return jdbcClient.sql("SELECT id, code, title, capacity, reserved_seats FROM section ORDER BY code")
            .query((row, rowNumber) -> toSection(row))
            .list();
    }

    /**
     * Returns early on no ids, since an empty IN list is not valid SQL.
     */
    @Override
    public List<Section> findSections(Set<UUID> sectionIds) {
        if (sectionIds.isEmpty()) {
            return List.of();
        }
        return jdbcClient.sql("""
                SELECT id, code, title, capacity, reserved_seats
                FROM section
                WHERE id IN (:ids)
                ORDER BY code
                """)
            .param("ids", sectionIds)
            .query((row, rowNumber) -> toSection(row))
            .list();
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

    @Override
    public void releaseSeats(UUID sectionId, int seats) {
        jdbcClient.sql("UPDATE section SET reserved_seats = reserved_seats - :seats WHERE id = :id")
            .param("id", sectionId)
            .param("seats", seats)
            .update();
    }

    private Section toSection(ResultSet row) throws SQLException {
        return new Section(
            row.getObject("id", UUID.class),
            row.getString("code"),
            row.getString("title"),
            row.getInt("capacity"),
            row.getInt("reserved_seats"));
    }
}
