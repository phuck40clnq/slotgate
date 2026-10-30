package com.example.seatinventory.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.example.seatinventory.application.port.out.SeatHoldRepositoryPort;
import com.example.seatinventory.domain.model.SeatHold;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatHoldPersistenceAdapter implements SeatHoldRepositoryPort {

    private final JdbcClient jdbcClient;

    @Override
    public void save(SeatHold seatHold) {
        jdbcClient.sql("""
                INSERT INTO seat_hold (id, section_id, student_id, held_at)
                VALUES (:id, :sectionId, :studentId, :heldAt)
                """)
            .param("id", seatHold.id())
            .param("sectionId", seatHold.sectionId())
            .param("studentId", seatHold.studentId())
            .param("heldAt", OffsetDateTime.ofInstant(seatHold.heldAt(), ZoneOffset.UTC))
            .update();
    }
}
