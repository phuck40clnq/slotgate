package com.example.seatinventory.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.seatinventory.domain.model.HoldPeriod;

@Configuration
@EnableConfigurationProperties(SeatHoldProperties.class)
@EnableScheduling
public class SeatHoldConfig {

    @Bean
    HoldPeriod holdPeriod(SeatHoldProperties properties) {
        return new HoldPeriod(properties.ttl());
    }
}
