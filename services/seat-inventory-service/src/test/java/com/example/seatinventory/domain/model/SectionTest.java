package com.example.seatinventory.domain.model;

import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @Test
    void opensWithEverySeatLeft() {
        Section section = Section.open(SECTION_ID, 40);

        assertThat(section.seatsLeft()).isEqualTo(40);
    }

    @Test
    void countsOnlyUnreservedSeatsAsLeft() {
        Section section = new Section(SECTION_ID, 40, 39);

        assertThat(section.seatsLeft()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsCapacityBelowOne(int capacity) {
        assertThatIllegalArgumentException().isThrownBy(() -> Section.open(SECTION_ID, capacity));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 41})
    void rejectsReservedSeatsOutsideCapacity(int reservedSeats) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Section(SECTION_ID, 40, reservedSeats));
    }
}
