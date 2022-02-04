package com.xantrix.webapp.appconf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application")
public class AppConfig {
    private String listino;

    public Double getSconto() {
        return sconto;
    }

    public void setSconto(Double sconto) {
        this.sconto = sconto;
    }

    private Double sconto = 0.00;

    public String getListino() {
        return listino;
    }

    public void setListino(String Listino) {
        this.listino = Listino;
    }
}
