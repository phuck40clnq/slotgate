package com.example.seatinventory.infrastructure.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatHoldPersistenceAdapter implements SeatHoldRepositoryPort {

    private final JdbcClient jdbcClient;

    /**
     * A concurrent insert for the same student and section waits on the unique index until the first one
     * commits or rolls back, then either skips or inserts. The conflict target repeats the predicate of the
     * partial unique index, so that PostgreSQL picks that index. Selecting from section skips an unknown section
     * instead of failing on the foreign key. See ADR-0006 and ADR-0007.
     */
    @Override
    public boolean saveIfAbsent(SeatHold seatHold) {
        int savedRows = jdbcClient.sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at, expires_at, status)
                SELECT :id, id, :studentId, :heldAt, :expiresAt, :status FROM section WHERE id = :sectionId
                ON CONFLICT (section_id, student_id) WHERE status IN ('HELD', 'CONFIRMED') DO NOTHING
                """)
            .param("id", seatHold.id())
            .param("sectionId", seatHold.sectionId())
            .param("studentId", seatHold.studentId())
            .param("heldAt", toTimestamp(seatHold.heldAt()))
            .param("expiresAt", toTimestamp(seatHold.expiresAt()))
            .param("status", seatHold.status().name())
            .update();
        return savedRows == 1;
    }

    @Override
    public Optional<SeatHold> findActiveHold(UUID sectionId, UUID studentId) {
        return jdbcClient.sql("""
                SELECT id, section_id, student_id, held_at, expires_at, status
                FROM seat_hold
                WHERE section_id = :sectionId AND student_id = :studentId AND status IN ('HELD', 'CONFIRMED')
                """)
            .param("sectionId", sectionId)
            .param("studentId", studentId)
            .query((row, rowNumber) -> toSeatHold(row))
            .optional();
    }

    @Override
    public Optional<SeatHold> findHold(UUID holdId) {
        return jdbcClient.sql("""
                SELECT id, section_id, student_id, held_at, expires_at, status
                FROM seat_hold
                WHERE id = :id
                """)
            .param("id", holdId)
            .query((row, rowNumber) -> toSeatHold(row))
            .optional();
    }

    /**
     * The status filter repeats the predicate of the partial index of migration V6 word for word, so that
     * PostgreSQL proves the index applies and reads only the student's entries.
     */
    @Override
    public List<SeatHold> findCurrentHolds(UUID studentId, Instant now) {
        return jdbcClient.sql("""
                SELECT id, section_id, student_id, held_at, expires_at, status
                FROM seat_hold
                WHERE student_id = :studentId AND status IN ('HELD', 'CONFIRMED')
                    AND (status = 'CONFIRMED' OR expires_at > :now)
                """)
            .param("studentId", studentId)
            .param("now", toTimestamp(now))
            .query((row, rowNumber) -> toSeatHold(row))
            .list();
    }

    /**
     * Check and change are one statement. When two statements change the same hold, the second waits for the
     * row lock, then PostgreSQL re-evaluates its WHERE clause on the committed row: the hold is no longer HELD,
     * so it changes nothing. See ADR-0007.
     */
    @Override
    public boolean confirm(UUID holdId, Instant now) {
        int confirmedRows = jdbcClient.sql("""
                UPDATE seat_hold
                SET status = 'CONFIRMED'
                WHERE id = :id AND status = 'HELD' AND expires_at > :now
                """)
            .param("id", holdId)
            .param("now", toTimestamp(now))
            .update();
        return confirmedRows == 1;
    }

    /**
     * Served by the partial index of migration V5, so it reads only the held holds that expired. The status stays a
     * literal that repeats the predicate of that index: with a bind parameter, a cached generic plan could not use
     * it. See ADR-0008.
     */
    @Override
    public List<UUID> findSectionsWithExpiredHolds(Instant now) {
        return jdbcClient.sql("""
                SELECT DISTINCT section_id
                FROM seat_hold
                WHERE status = 'HELD' AND expires_at <= :now
                """)
            .param("now", toTimestamp(now))
            .query(UUID.class)
            .list();
    }

    /**
     * Same primitive as {@link #confirm}: only rows still HELD change, and a row confirmed meanwhile is
     * re-evaluated after its lock is released and skipped. See ADR-0007.
     */
    @Override
    public int expireHolds(UUID sectionId, Instant now) {
        return jdbcClient.sql("""
                UPDATE seat_hold
                SET status = 'EXPIRED'
                WHERE section_id = :sectionId AND status = 'HELD' AND expires_at <= :now
                """)
            .param("sectionId", sectionId)
            .param("now", toTimestamp(now))
            .update();
    }

    private static OffsetDateTime toTimestamp(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private SeatHold toSeatHold(ResultSet row) throws SQLException {
        return new SeatHold(
            row.getObject("id", UUID.class),
            row.getObject("section_id", UUID.class),
            row.getObject("student_id", UUID.class),
            row.getObject("held_at", OffsetDateTime.class).toInstant(),
            row.getObject("expires_at", OffsetDateTime.class).toInstant(),
            SeatHoldStatus.valueOf(row.getString("status")));
    }
}
