package com.example.seatinventory.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.InMemorySeatInventory;

class ListSectionsServiceTest {

    private static final UUID FIRST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final ListSectionsService service = new ListSectionsService(seatInventory);

    @Test
    void listsSectionsByCodeWithTheSeatsLeft() {
        seatInventory.save(new Section(SECOND_ID, "SIM202 1", "Second course", 30, 30));
        seatInventory.save(new Section(FIRST_ID, "SIM101 1", "First course", 40, 1));

        assertThat(service.listSections()).containsExactly(
            new SectionResult(FIRST_ID, "SIM101 1", "First course", 40, 39),
            new SectionResult(SECOND_ID, "SIM202 1", "Second course", 30, 0));
    }

    @Test
    void listsNothingWhenNoSectionIsOpen() {
        assertThat(service.listSections()).isEmpty();
    }
}
