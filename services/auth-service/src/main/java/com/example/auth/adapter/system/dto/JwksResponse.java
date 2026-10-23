package com.example.auth.adapter.system.dto;

import java.util.List;
import java.util.Map;

public record JwksResponse(
    List<Map<String, Object>> keys
) {}
