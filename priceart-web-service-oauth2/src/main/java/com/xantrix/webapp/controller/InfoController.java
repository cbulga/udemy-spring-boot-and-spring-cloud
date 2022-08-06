package com.xantrix.webapp.controller;

import com.xantrix.webapp.appconf.AppConfig;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InfoController {

    private final AppConfig configuration;

    public InfoController(AppConfig configuration) {
        this.configuration = configuration;
    }

    @GetMapping("/info")
    @RefreshScope
    public Map<String, String> getInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("listino", configuration.getListino());
        return map;
    }
}
