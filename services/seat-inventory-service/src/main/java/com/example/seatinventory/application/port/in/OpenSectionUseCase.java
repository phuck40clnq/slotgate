package com.example.seatinventory.application.port.in;

import com.example.seatinventory.application.dto.OpenSectionCommand;
import com.example.seatinventory.application.dto.SectionResult;

/**
 * Opens a section with all of its seats free.
 */
public interface OpenSectionUseCase {

    SectionResult openSection(OpenSectionCommand command);
}
