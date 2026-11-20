package com.example.seatinventory.scenario;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;
import com.example.seatinventory.domain.model.SeatHoldStatus;

/**
 * The obvious but wrong confirmation and expiry: the status is read and checked in Java, then written by id
 * without a condition, so a confirmation and a sweep both act on the same stale HELD status. Exists only to
 * reproduce {@link ConfirmExpiryRaceScenario}.
 */
class CheckThenWriteSeatHolds implements SeatHoldRepositoryPort {

    private final SeatHoldRepositoryPort seatHolds;
    private final JdbcClient jdbcClient;

    CheckThenWriteSeatHolds(SeatHoldRepositoryPort seatHolds, JdbcClient jdbcClient) {
        this.seatHolds = seatHolds;
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean saveIfAbsent(SeatHold seatHold) {
        return seatHolds.saveIfAbsent(seatHold);
    }

    @Override
    public Optional<SeatHold> findActiveHold(UUID sectionId, UUID studentId) {
        return seatHolds.findActiveHold(sectionId, studentId);
    }

    @Override
    public Optional<SeatHold> findHold(UUID holdId) {
        return seatHolds.findHold(holdId);
    }

    @Override
    public List<SeatHold> findCurrentHolds(UUID studentId, Instant now) {
        return seatHolds.findCurrentHolds(studentId, now);
    }

    @Override
    public boolean confirm(UUID holdId, Instant now) {
        SeatHold seatHold = findHold(holdId).orElseThrow();
        if (seatHold.status() != SeatHoldStatus.HELD || !seatHold.expiresAt().isAfter(now)) {
            return false;
        }
        setStatus(holdId, SeatHoldStatus.CONFIRMED);
        return true;
    }

    @Override
    public List<UUID> findSectionsWithExpiredHolds(Instant now) {
        return seatHolds.findSectionsWithExpiredHolds(now);
    }

    @Override
    public int expireHolds(UUID sectionId, Instant now) {
        List<UUID> expiredHoldIds = jdbcClient.sql("""
                SELECT id FROM seat_hold
                WHERE section_id = :sectionId AND status = 'HELD' AND expires_at <= :now
                """)
            .param("sectionId", sectionId)
            .param("now", OffsetDateTime.ofInstant(now, ZoneOffset.UTC))
            .query(UUID.class)
            .list();
        expiredHoldIds.forEach(holdId -> setStatus(holdId, SeatHoldStatus.EXPIRED));
        return expiredHoldIds.size();
    }

    private void setStatus(UUID holdId, SeatHoldStatus status) {
        jdbcClient.sql("UPDATE seat_hold SET status = :status WHERE id = :id")
            .param("status", status.name())
            .param("id", holdId)
            .update();
    }
}
