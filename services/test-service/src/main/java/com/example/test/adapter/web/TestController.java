package com.example.test.adapter.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.test.adapter.web.dto.CallerResponse;
import com.example.test.common.error.TestBusinessErrorCode;
import com.example.test.common.exception.BusinessException;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private static final String INTERNAL_USER_ID = "X-Internal-User-Id";

    @GetMapping(path = "/me", headers = INTERNAL_USER_ID)
    public CallerResponse getCaller(@RequestHeader(INTERNAL_USER_ID) UUID userId) {
        return new CallerResponse(userId);
    }

    @GetMapping(path = "/me", headers = "!" + INTERNAL_USER_ID)
    public ResponseEntity<Void> getCallerWithoutId() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/errors/bad-request")
    public void simulateBadRequest() {
        throw new BusinessException(TestBusinessErrorCode.SIMULATED_BAD_REQUEST);
    }

    @GetMapping("/errors/not-found")
    public void simulateNotFound() {
        throw new BusinessException(TestBusinessErrorCode.SIMULATED_NOT_FOUND);
    }

    @GetMapping("/errors/internal")
    public void simulateInternalError() {
        throw new IllegalStateException("Simulated failure that no rule expects");
    }
}
