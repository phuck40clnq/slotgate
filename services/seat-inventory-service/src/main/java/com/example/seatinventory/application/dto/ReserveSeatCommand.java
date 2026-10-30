package com.example.seatinventory.application.dto;

import java.util.UUID;

public record ReserveSeatCommand(UUID sectionId, UUID studentId) {
}
