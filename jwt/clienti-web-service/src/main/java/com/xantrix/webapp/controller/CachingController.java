package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.InfoMsg;
import com.xantrix.webapp.service.ClientiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/api/clienti-cache")
@Slf4j
public class CachingController {

    private final ClientiService clientiService;

    public CachingController(ClientiService clientiService) {
        this.clientiService = clientiService;
    }

    @GetMapping(value = "clearAllCaches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> clearAllCaches() {
        log.info("************** Clearing all caches *************");
        clientiService.cleanCaches();
        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .message("Eliminazione della cache eseguita con successo!")
                .build(), HttpStatus.OK);
    }
}
