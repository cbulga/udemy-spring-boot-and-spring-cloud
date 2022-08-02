package com.xantrix.webapp.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sicurezza")
@Data
public class JwtConfig {

    private String header;
    private String secret;
}
