package com.xantrix.webapp.component;

import com.xantrix.webapp.repository.ArticoliRepository;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Component used by actuator info endpoint.
 */
//@Component
public class ArtInfoContributor implements InfoContributor {

    private final ArticoliRepository articoliRepository;

    public ArtInfoContributor(ArticoliRepository articoliRepository) {
        this.articoliRepository = articoliRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {
//        long qtaArticoli = articoliRepository.findAll().size();
        long qtaArticoli = articoliRepository.count();
        Map<String, Object> articoliMap = new HashMap<>();
        articoliMap.put("Qta Articoli", qtaArticoli);
        builder.withDetail("articoli-info", articoliMap);
    }
}
