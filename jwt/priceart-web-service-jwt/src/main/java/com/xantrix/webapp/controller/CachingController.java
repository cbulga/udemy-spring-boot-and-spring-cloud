package com.xantrix.webapp.controller;

import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.service.PrezziService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/prezzi-cache")
@Slf4j
public class CachingController {

    private final PrezziService prezziService;

    public CachingController(PrezziService prezziService) {
        this.prezziService = prezziService;
    }

    @GetMapping(value = "clearAllCaches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> clearAllCaches() {
        log.info("Clearing all caches");
        prezziService.cleanCaches();
        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .message("Eliminazione della cache eseguita con successo!")
                .build(), HttpStatus.OK);
    }
}
