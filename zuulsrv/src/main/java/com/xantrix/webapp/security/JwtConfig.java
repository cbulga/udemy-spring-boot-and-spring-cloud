package com.xantrix.webapp.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConfigurationProperties("sicurezza")
@Data
public class JwtConfig implements Serializable {

    private static final long serialVersionUID = 6544402944939783131L;
    private String uri;
    private String header;
    private String prefix;
    private String secret;
    private int expiration;
}
