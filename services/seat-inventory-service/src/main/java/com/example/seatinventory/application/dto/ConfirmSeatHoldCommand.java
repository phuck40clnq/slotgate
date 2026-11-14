package com.example.seatinventory.application.dto;

import java.util.UUID;

public record ConfirmSeatHoldCommand(UUID sectionId, UUID holdId, UUID studentId) {
}
