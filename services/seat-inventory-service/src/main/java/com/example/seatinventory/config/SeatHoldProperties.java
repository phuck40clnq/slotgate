package com.example.seatinventory.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "seat-hold")
@Validated
public record SeatHoldProperties(@NotNull Duration ttl) {
}
