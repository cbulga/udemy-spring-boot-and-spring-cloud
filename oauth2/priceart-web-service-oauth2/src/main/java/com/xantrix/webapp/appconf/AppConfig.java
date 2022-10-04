package com.xantrix.webapp.appconf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
@Data
public class AppConfig {

    private String listino;
    private Double sconto = 0.00;
    private int tipo;
}
