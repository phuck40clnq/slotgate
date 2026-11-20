package com.example.seatinventory.application.usecase;

import static com.example.seatinventory.support.SectionFixtures.SECTION_CODE;
import static com.example.seatinventory.support.SectionFixtures.SECTION_ID;
import static com.example.seatinventory.support.SectionFixtures.SECTION_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.SectionResult;
import com.example.seatinventory.common.error.SeatInventoryBusinessErrorCode;
import com.example.seatinventory.common.exception.BusinessException;
import com.example.seatinventory.domain.model.Section;
import com.example.seatinventory.support.InMemorySeatInventory;

class OpenSectionServiceTest {

    private static final UUID OTHER_SECTION_ID = UUID.fromString("00000000-0000-0000-0000-0000000000ff");
    private static final OpenSectionCommand COMMAND = new OpenSectionCommand(SECTION_CODE, SECTION_TITLE, 40);

    private final InMemorySeatInventory seatInventory = new InMemorySeatInventory();
    private final OpenSectionService service = new OpenSectionService(seatInventory, () -> SECTION_ID);

    @Test
    void opensSectionWithEverySeatLeft() {
        SectionResult result = service.openSection(COMMAND);

        assertThat(result).isEqualTo(new SectionResult(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 40));
    }

    @Test
    void storesTheOpenedSection() {
        service.openSection(COMMAND);

        assertThat(seatInventory.findSection(SECTION_ID))
            .contains(new Section(SECTION_ID, SECTION_CODE, SECTION_TITLE, 40, 0));
    }

    @Test
    void rejectsACodeThatAnotherSectionHas() {
        seatInventory.save(new Section(OTHER_SECTION_ID, SECTION_CODE, "Another course", 10, 0));

        BusinessException rejection = catchThrowableOfType(
            BusinessException.class, () -> service.openSection(COMMAND));

        assertThat(rejection.getErrorCode()).isEqualTo(SeatInventoryBusinessErrorCode.SECTION_CODE_TAKEN);
        assertThat(seatInventory.findSection(SECTION_ID)).isEmpty();
    }
}
