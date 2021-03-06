package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.service.PrezziService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/prezzi")
@Slf4j
@Tag(name = "PrezziController", description = "Controller Operazioni di ottenimento e eliminazione prezzo articolo")
public class PrezziController {

    private final PrezziService prezziService;
    private final AppConfig config;

    public PrezziController(PrezziService prezziService, AppConfig config) {
        this.prezziService = prezziService;
        this.config = config;
    }

    // ------------------- SELECT PREZZO CODART SENZA AUTENTICAZIONE ------------------------------------
    @GetMapping(value = {"/noauth/{codart}/{idlist}", "noauth/{codart}"})
    public double getPriceCodArtNoAuth(@PathVariable("codart") String codArt,
                                       @PathVariable("idlist") Optional<String> optIdList) {
        double retVal = 0;

        String idList = optIdList.orElseGet(config::getListino);

        log.info("Listino di Riferimento: {}", idList);

        DettListini prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: " + prezzo.getPrezzo());
            retVal = prezzo.getPrezzo();
        } else {
            log.warn("Prezzo Articolo Assente!!");
        }

        return retVal;
    }

    // ------------------- SELECT PREZZO CODART ------------------------------------
    @Operation(
            summary = "Ricerca il PREZZO dell'articolo selezionato",
            description = "pprevede il parametro opzionale idlist",
            tags = {"Prezzi"})
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Il Prezzo ?? stato trovato!"),
            })
    @GetMapping(value = {"/{codart}/{idlist}", "/{codart}"})
    @RefreshScope
    public double getPriceCodArt(@Parameter(description = "Codice Articolo", required = true) @PathVariable("codart") String codArt,
                                 @Parameter(description = "ID Listino") @PathVariable("idlist") Optional<String> optIdList) {
        double retVal = 0;

        String idList = optIdList.orElseGet(config::getListino);

        log.info("Listino di Riferimento: " + idList);

        DettListini prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: {}", prezzo.getPrezzo());
            double sconto = config.getSconto();
            if (sconto > 0)
                log.info("Attivato sconto {}%", sconto);
            retVal = Math.round(prezzo.getPrezzo() * (1 - (sconto / 100)) * 100) / 100.0;
        } else
            log.warn("Prezzo Articolo Assente!!");

        return retVal;
    }

    // ------------------- DELETE PREZZO LISTINO ------------------------------------
    @RequestMapping(value = "/elimina/{codart}/{idlist}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePrice(@PathVariable("codart") String codArt, @PathVariable("idlist") String idList) {
        log.info(String.format("Eliminazione prezzo listino %s dell'articolo %s", idList, codArt));

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();

        prezziService.delPrezzo(codArt, idList);

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Prezzo Eseguita Con Successo");

        log.info("Eliminazione Prezzo Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }
}
