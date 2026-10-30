package com.example.seatinventory.adapter.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OpenSectionRequest(@NotNull @Min(1) Integer capacity) {
}
