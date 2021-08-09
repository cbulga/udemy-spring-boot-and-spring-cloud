package com.xantrix.webapp.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("gestuser")
@Data
public class UserConfig {

    private String userId;
    private String password;
    private String srvUrl;
}
