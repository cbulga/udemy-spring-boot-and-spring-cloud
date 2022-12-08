package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.InfoMsg;
import com.xantrix.webapp.service.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/promo-cache")
@Slf4j
public class CachingController {

    private final PromoService promoService;

    public CachingController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping(value = "clearAllCaches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> clearAllCaches() {
        log.info("************** Clearing all caches *************");
        promoService.cleanCaches();
        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .message("Eliminazione della cache eseguita con successo!")
                .build(), HttpStatus.OK);
    }
}
