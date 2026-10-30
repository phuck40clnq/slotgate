package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.InMemorySeatInventory;

class OpenSectionServiceTest {

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final OpenSectionService service = new OpenSectionService(seatInventory, () -> SECTION_ID);

    @Test
    void opensSectionWithEverySeatLeft() {
        SectionResult result = service.openSection(new OpenSectionCommand(40));

        assertThat(result).isEqualTo(new SectionResult(SECTION_ID, 40, 40));
    }

    @Test
    void storesTheOpenedSection() {
        service.openSection(new OpenSectionCommand(40));

        assertThat(seatInventory.findSection(SECTION_ID)).contains(new Section(SECTION_ID, 40, 0));
    }
}
