package com.example.auth.adapter.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.adapter.system.dto.JwksResponse;
import com.example.auth.infrastructure.security.jwt.jwks.JwksService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwksService jwksService;

    @GetMapping("/oauth2/jwks")
    public JwksResponse jwks() {
        return JwksMapper.toResponse(
            jwksService.getKeyDescriptors()
        );
    }
    
}
