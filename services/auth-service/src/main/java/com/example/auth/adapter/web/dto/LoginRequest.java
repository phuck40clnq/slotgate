package com.example.auth.adapter.web.dto;

public record LoginRequest (
    String email,
    String password
) {}
