package com.campus.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "your-secret-key";
    private long expiration = 604800000L;  // 7天
    private String header = "Authorization";
    private String tokenPrefix = "Bearer ";
} 