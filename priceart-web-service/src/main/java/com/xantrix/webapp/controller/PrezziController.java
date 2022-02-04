package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.service.PrezziService;
import io.swagger.annotations.*;
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
@Api(value = "alphashop", tags = "Controller Operazioni di ottenimento e eliminazione prezzo articolo")
@ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
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
    @ApiOperation(
            value = "Ricerca il PREZZO dell'articolo selezionato",
            notes = "pprevede il parametro opzionale idlist",
            response = Double.class,
            produces = "application/json")
    @ApiResponses(value =
            {@ApiResponse(code = 200, message = "Il Prezzo è stato trovato!"),
            })
    @GetMapping(value = {"/{codart}/{idlist}", "/{codart}"})
    @RefreshScope
    public double getPriceCodArt(@PathVariable("codart") String codArt, @PathVariable("idlist") Optional<String> optIdList) {
        double retVal = 0;

        String idList = optIdList.orElseGet(config::getListino);

        log.info("Listino di Riferimento: " + idList);

        DettListini prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: {}", prezzo.getPrezzo());
            double sconto = config.getSconto();
            if (sconto > 0)
                log.info("Attivato sconto {}%", sconto);
            retVal = prezzo.getPrezzo() * (1 - (sconto / 100));
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
