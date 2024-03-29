package com.xantrix.webapp.controller;

import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.service.ArticoliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/articoli-cache")
@Slf4j
public class CachingController {

    private final ArticoliService articoliService;

    public CachingController(ArticoliService articoliService) {
        this.articoliService = articoliService;
    }

    @GetMapping(value = "clearAllCaches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> clearAllCaches() {
        log.info("Clearing all caches");
        articoliService.cleanCaches();
        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .message("Eliminazione della cache eseguita con successo!")
                .build(), HttpStatus.OK);
    }
}
