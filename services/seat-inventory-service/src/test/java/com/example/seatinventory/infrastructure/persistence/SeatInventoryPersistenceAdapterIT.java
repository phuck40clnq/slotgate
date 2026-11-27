package com.example.seatinventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

import com.example.seatinventory.application.port.out.SeatInventoryPort;
import com.example.seatinventory.domain.model.SeatClaim;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.PostgresIntegrationTest;
import com.example.seatinventory.support.SectionFixtures;

@RequiredArgsConstructor
class SeatInventoryPersistenceAdapterIT extends PostgresIntegrationTest {

    private final SeatInventoryPort seatInventory;

    private final JdbcClient jdbcClient;

    @Test
    void savesASectionWithItsCodeAndTitle() {
        Section section = SectionFixtures.section(UUID.randomUUID(), 10, 0);

        boolean isSaved = seatInventory.saveIfCodeIsFree(section);

        assertThat(isSaved).isTrue();
        assertThat(seatInventory.findSection(section.id())).contains(section);
    }

    @Test
    void skipsASectionWhoseCodeIsTaken() {
        Section first = openSection(10, 0);
        Section second = new Section(UUID.randomUUID(), first.code(), "Another course", 5, 0);

        boolean isSaved = seatInventory.saveIfCodeIsFree(second);

        assertThat(isSaved).isFalse();
        assertThat(seatInventory.findSection(second.id())).isEmpty();
    }

    @Test
    void databaseRefusesASecondSectionWithTheSameCode() {
        Section first = openSection(10, 0);

        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> jdbcClient
            .sql("INSERT INTO section (id, code, title, capacity) VALUES (:id, :code, 'Another course', 5)")
            .param("id", UUID.randomUUID())
            .param("code", first.code())
            .update());
    }

    @Test
    void databaseRefusesACodeTheServiceCouldNotRead() {
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> jdbcClient
            .sql("INSERT INTO section (id, code, title, capacity) VALUES (:id, :code, 'Course', 5)")
            .param("id", UUID.randomUUID())
            .param("code", "X".repeat(33))
            .update());
    }

    @Test
    void listsSectionsByCode() {
        Section later = save(new Section(UUID.randomUUID(), "LIST-B", "Listed second", 5, 1));
        Section earlier = save(new Section(UUID.randomUUID(), "LIST-A", "Listed first", 5, 0));

        List<Section> sections = seatInventory.listSections();

        assertThat(sections)
            .filteredOn(section -> section.code().startsWith("LIST-"))
            .containsExactly(earlier, later);
    }

    @Test
    void findsTheGivenSectionsByCodeLeavingOutUnknownIds() {
        Section later = save(new Section(UUID.randomUUID(), "FIND-B", "Found second", 5, 1));
        Section earlier = save(new Section(UUID.randomUUID(), "FIND-A", "Found first", 5, 0));
        openSection(5, 0);
        UUID unknownId = UUID.fromString("00000000-0000-0000-0000-0000000000ff");

        List<Section> sections = seatInventory.findSections(Set.of(later.id(), earlier.id(), unknownId));

        assertThat(sections).containsExactly(earlier, later);
    }

    @Test
    void findsNoSectionForNoId() {
        assertThat(seatInventory.findSections(Set.of())).isEmpty();
    }

    @Test
    void claimsSeatWhileSeatsAreLeft() {
        Section section = openSection(1, 0);

        SeatClaim claim = seatInventory.claimSeat(section.id());

        assertThat(claim).isEqualTo(new SeatClaim.Claimed());
        assertThat(seatInventory.findSection(section.id())).contains(withReservedSeats(section, 1));
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

    @Test
    void givesSeatsBack() {
        Section section = openSection(10, 7);

        seatInventory.releaseSeats(section.id(), 3);

        assertThat(seatInventory.findSection(section.id())).contains(withReservedSeats(section, 4));
    }

    @Test
    void databaseRefusesToGiveBackMoreSeatsThanReserved() {
        Section section = openSection(10, 2);

        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> seatInventory.releaseSeats(section.id(), 3));
    }

    private Section openSection(int capacity, int reservedSeats) {
        return save(SectionFixtures.section(UUID.randomUUID(), capacity, reservedSeats));
    }

    private Section save(Section section) {
        seatInventory.saveIfCodeIsFree(section);
        return section;
    }

    private static Section withReservedSeats(Section section, int reservedSeats) {
        return new Section(section.id(), section.code(), section.title(), section.capacity(), reservedSeats);
    }
}
