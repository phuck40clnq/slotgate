package com.example.auth.domain.security;

import java.util.UUID;

public record DomainTokenSubject (
    UUID userId
) {}
