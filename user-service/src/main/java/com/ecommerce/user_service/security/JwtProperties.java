package com.ecommerce.user_service.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer;

    private long accessTokenExpiration;

    private long refreshTokenExpiration;
}