package com.example.idp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "idp")
public class AuthProperties {
    private String issuer;
    private Duration accessTokenTtl;
    private Duration refreshTokenTtl;
    private int rsaKeySize;
    private int keyRotationDays;
    private int keyRetentionDays;

}
