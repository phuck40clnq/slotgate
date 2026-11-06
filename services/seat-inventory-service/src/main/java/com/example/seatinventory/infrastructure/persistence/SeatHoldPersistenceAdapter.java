package com.example.seatinventory.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatHoldPersistenceAdapter implements SeatHoldRepositoryPort {

    private final JdbcClient jdbcClient;

    /**
     * A concurrent insert for the same student and section waits on the unique index until the first one
     * commits or rolls back, then either skips or inserts. Selecting from section skips an unknown section
     * instead of failing on the foreign key. See ADR-0006.
     */
    @Override
    public boolean saveIfAbsent(SeatHold seatHold) {
        int savedRows = jdbcClient.sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at)
                SELECT :id, id, :studentId, :heldAt FROM section WHERE id = :sectionId
                ON CONFLICT (section_id, student_id) DO NOTHING
                """)
            .param("id", seatHold.id())
            .param("sectionId", seatHold.sectionId())
            .param("studentId", seatHold.studentId())
            .param("heldAt", OffsetDateTime.ofInstant(seatHold.heldAt(), ZoneOffset.UTC))
            .update();
        return savedRows == 1;
    }

    @Override
    public Optional<SeatHold> findHold(UUID sectionId, UUID studentId) {
        return jdbcClient.sql("""
                SELECT id, section_id, student_id, held_at
                FROM seat_hold
                WHERE section_id = :sectionId AND student_id = :studentId
                """)
            .param("sectionId", sectionId)
            .param("studentId", studentId)
            .query((row, rowNumber) -> toSeatHold(row))
            .optional();
    }

    private SeatHold toSeatHold(ResultSet row) throws SQLException {
        return new SeatHold(
            row.getObject("id", UUID.class),
            row.getObject("section_id", UUID.class),
            row.getObject("student_id", UUID.class),
            row.getObject("held_at", OffsetDateTime.class).toInstant());
    }
}
