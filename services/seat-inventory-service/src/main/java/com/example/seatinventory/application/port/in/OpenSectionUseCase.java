package com.example.seatinventory.application.port.in;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.SectionResult;

/**
 * Opens a section with all of its seats free, under a code that no other section has.
 */
public interface OpenSectionUseCase {

    /**
     * @throws com.example.seatinventory.common.exception.BusinessException SECTION_CODE_TAKEN
     */
    SectionResult openSection(OpenSectionCommand command);
}
