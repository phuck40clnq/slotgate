package com.example.seatinventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.PostgresIntegrationTest;

class SeatInventoryPersistenceAdapterIT extends PostgresIntegrationTest {

    @Autowired
    private SeatInventoryPort seatInventory;

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void claimsSeatWhileSeatsAreLeft() {
        Section section = openSection(1, 0);

        SeatClaim claim = seatInventory.claimSeat(section.id());

        assertThat(claim).isEqualTo(new SeatClaim.Claimed());
        assertThat(seatInventory.findSection(section.id())).contains(new Section(section.id(), 1, 1));
    }

    @Test
    void reportsFullSectionWithoutChangingIt() {
        Section section = openSection(1, 1);

        SeatClaim claim = seatInventory.claimSeat(section.id());

        assertThat(claim).isEqualTo(new SeatClaim.SectionFull());
        assertThat(seatInventory.findSection(section.id())).contains(section);
    }

    @Test
    void reportsUnknownSection() {
        SeatClaim claim = seatInventory.claimSeat(UUID.fromString("00000000-0000-0000-0000-0000000000ff"));

        assertThat(claim).isEqualTo(new SeatClaim.SectionNotFound());
    }

    @Test
    void databaseRefusesMoreReservedSeatsThanCapacity() {
        Section section = openSection(1, 1);

        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> jdbcClient
            .sql("UPDATE section SET reserved_seats = capacity + 1 WHERE id = :id")
            .param("id", section.id())
            .update());
    }

    private Section openSection(int capacity, int reservedSeats) {
        Section section = new Section(UUID.randomUUID(), capacity, reservedSeats);
        seatInventory.save(section);
        return section;
    }
}
