package com.example.seatinventory.domain.model;

import static com.example.seatinventory.support.SectionFixtures.SECTION_CODE;
import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static com.example.seatinventory.support.SectionFixtures.SECTION_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @Test
    void opensWithEverySeatLeft() {
        Section section = Section.open(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40);

        assertThat(section.seatsLeft()).isEqualTo(40);
    }

    @Test
    void countsOnlyUnreservedSeatsAsLeft() {
        Section section = new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 39);

        assertThat(section.seatsLeft()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsCapacityBelowOne(int capacity) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Section.open(SECTION_ID, SECTION_CODE, SECTION_TITLE, capacity));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 41})
    void rejectsReservedSeatsOutsideCapacity(int reservedSeats) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, reservedSeats));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "SIM-CODE-LONGER-THAN-THIRTY-TWO-X"})
    void rejectsACodeThatIsBlankOrTooLong(String code) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Section.open(SECTION_ID, code, SECTION_TITLE, 40));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void rejectsABlankTitle(String title) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> Section.open(SECTION_ID, SECTION_CODE, title, 40));
    }
}
