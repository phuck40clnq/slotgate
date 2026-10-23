package com.example.auth.infrastructure.security.jwt;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.security.jwt")
@Getter @Setter
public class JwtProperties {

    private String issuer;
    private long accessTokenExp;
    private List<String> audiences;
    private Resource privateKey;
    private Resource publicKey;

}
