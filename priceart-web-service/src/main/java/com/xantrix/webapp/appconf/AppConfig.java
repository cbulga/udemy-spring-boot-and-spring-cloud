package com.xantrix.webapp.appconf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class AppConfig {
    private String listino;

    public String getListino() {
        return listino;
    }

    public void setListino(String Listino) {
        this.listino = Listino;
    }
}
