package com.xantrix.webapp.component;

import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.repository.ListinoRepository;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PriceInfoContributor implements InfoContributor {

    private final AppConfig configuration;
    private final ListinoRepository prezzi;

    public PriceInfoContributor(ListinoRepository prezzi, AppConfig configuration) {
        this.prezzi = prezzi;
        this.configuration = configuration;
    }

    @Override
    public void contribute(Builder builder) {
        String listinoId = configuration.getListino();
        long qtaPrezzi = prezzi.getQtaDettList(listinoId);

        Map<String, Object> priceMap = new HashMap<>();
        priceMap.put("listino", listinoId);
        priceMap.put("qta", qtaPrezzi);
        builder.withDetail("prezzi-info", priceMap);
    }
}