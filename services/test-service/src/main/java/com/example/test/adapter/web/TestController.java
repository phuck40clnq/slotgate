package com.example.test.adapter.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/test")
public class TestController {
    
    @GetMapping("/hello")
    public Map<String, String> hello(
            @RequestHeader(value = "X-User-Id", required = true) String userId
    ) {
        return Map.of(
            "userId", String.valueOf(userId)
        );
    }

    @GetMapping("/error-400")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest() {
        return Map.of(
            "code", "INVALID_INPUT",
            "message", "Input is invalid"
        );
    }

    @GetMapping("/error-404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound() {
        return Map.of(
            "code", "USER_NOT_FOUND",
            "message", "User not found"
        );
    }

    @GetMapping("/error-500")
    public void serverError() {
        throw new RuntimeException(
            "Simulated internal crash"
        );
    }
    

}
