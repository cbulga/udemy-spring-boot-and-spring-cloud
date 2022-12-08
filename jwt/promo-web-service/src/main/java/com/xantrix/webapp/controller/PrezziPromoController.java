package com.xantrix.webapp.controller;

import com.xantrix.webapp.service.PrezziPromoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/promo/prezzo")
@Slf4j
public class PrezziPromoController {

    private final PrezziPromoService prezziPromoService;

    public PrezziPromoController(PrezziPromoService prezziPromoService) {
        this.prezziPromoService = prezziPromoService;
    }

    @GetMapping(value = "/{codArt}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> getPromoCodArt(@PathVariable("codArt") @NotNull String codArt) {
        log.info("****** Otteniamo il prezzo della promo attiva con codArt {} ******", codArt);
        return ResponseEntity.ok(prezziPromoService.selByCodArtAndPromoAttiva(codArt));
    }

    @GetMapping(value = "/fidelity/{codArt}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> getPromoCodArtFid(@PathVariable("codArt") @NotNull String codArt) {
        log.info("****** Otteniamo il prezzo della promo attiva riservata a fidelity con codArt {} ******", codArt);
        return ResponseEntity.ok(prezziPromoService.selByCodArtAndFidAndPromoAttiva(codArt));
    }

    @GetMapping(value = "/{codArt}/{codFid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> getPromoCodArtCodFid(@PathVariable("codArt") @NotNull String codArt, @PathVariable("codFid") String codFid) {
        log.info("****** Otteniamo il prezzo della promo attiva riservata a fidelity con codArt {} e codFid {} ******", codArt, codFid);
        return ResponseEntity.ok(prezziPromoService.selByCodArtAndCodFidAndPromoAttiva(codArt, codFid));
    }
}
