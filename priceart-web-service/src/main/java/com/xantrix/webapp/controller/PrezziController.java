package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.service.PrezziService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/prezzi")
public class PrezziController {

    private static final Logger logger = LoggerFactory.getLogger(PrezziController.class);
    private final PrezziService prezziService;
    private final AppConfig config;

    public PrezziController(PrezziService prezziService, AppConfig config) {
        this.prezziService = prezziService;
        this.config = config;
    }

    // ------------------- SELECT PREZZO CODART ------------------------------------
    @GetMapping(value = {"/{codart}/{idlist}", "/{codart}"})
    public double getPriceCodArt(@PathVariable("codart") String codArt, @PathVariable("idlist") Optional<String> optIdList) {
        double retVal = 0;

        String idList = optIdList.orElseGet(config::getListino);

        logger.info("Listino di Riferimento: " + idList);

        DettListini prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            logger.info("Prezzo Articolo: " + prezzo.getPrezzo());
            retVal = prezzo.getPrezzo();
        } else
            logger.warn("Prezzo Articolo Assente!!");

        return retVal;
    }

    // ------------------- DELETE PREZZO LISTINO ------------------------------------
    @RequestMapping(value = "/elimina/{codart}/{idlist}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePrice(@PathVariable("codart") String codArt, @PathVariable("idlist") String idList) {
        logger.info(String.format("Eliminazione prezzo listino %s dell'articolo %s", idList, codArt));

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();

        prezziService.delPrezzo(codArt, idList);

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Prezzo Eseguita Con Successo");

        logger.info("Eliminazione Prezzo Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }
}
