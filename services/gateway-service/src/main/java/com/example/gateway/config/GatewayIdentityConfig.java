package com.example.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.gateway.filter.identity.IdentitySkipRouteMatcher;

@Configuration
@EnableConfigurationProperties(IdentitySkipRouteMatcher.class)
public class GatewayIdentityConfig {}
