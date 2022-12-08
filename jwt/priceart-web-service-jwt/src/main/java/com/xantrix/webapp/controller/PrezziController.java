package com.xantrix.webapp.controller;

import com.xantrix.webapp.appconf.AppConfig;
import com.xantrix.webapp.dtos.DettListiniDTO;
import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.dtos.PrezzoDTO;
import com.xantrix.webapp.service.PrezziService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/prezzi")
@Slf4j
@Tag(name = "PrezziController", description = "Controller Operazioni di ottenimento e eliminazione prezzo articolo")
public class PrezziController {

    public static final String PREZZO_ARTICOLO_ASSENTE = "Prezzo Articolo Assente!!";
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

        DettListiniDTO prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: " + prezzo.getPrezzo());
            retVal = prezzo.getPrezzo();
        } else {
            log.warn(PREZZO_ARTICOLO_ASSENTE);
        }

        return retVal;
    }

    // ------------------- SELECT PREZZO CODART ------------------------------------
    @Operation(
            summary = "Ricerca il PREZZO dell'articolo selezionato",
            description = "prevede il parametro opzionale idlist",
            tags = {"Prezzi"})
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Il Prezzo è stato trovato!"),
            })
    @GetMapping(value = {"/{codart}/{idlist}", "/{codart}"})
    // RefreshScope: per fare in modo che questo bean (metodo del bean) possa essere refreshato a runtime quando c'e'
    //               un cambio configurazione, creando una nuova istanza di bean che abbia recepito la nuova configurazione (con il nuovo sconto)
    @RefreshScope
    public double getPriceCodArt(@Parameter(description = "Codice Articolo", required = true) @PathVariable("codart") String codArt,
                                 @Parameter(description = "ID Listino") @PathVariable("idlist") Optional<String> optIdList) {
        double retVal = 0;

        String idList = optIdList.orElseGet(config::getListino);

        log.info("Listino di Riferimento: " + idList);

        DettListiniDTO prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: {}", prezzo.getPrezzo());
            double sconto = config.getSconto();
            if (sconto > 0)
                log.info("Attivato sconto {}%", sconto);
            retVal = Math.round(prezzo.getPrezzo() * (1 - (sconto / 100)) * 100) / 100.0;
        } else
            log.warn(PREZZO_ARTICOLO_ASSENTE);

        return retVal;
    }


    @RefreshScope
    @GetMapping(value = {"info/{codart}/{idlist}", "info/{codart}"})
    public ResponseEntity<PrezzoDTO> getPriceCodArt2(@PathVariable("codart") String codArt,
                                                     @PathVariable("idlist") Optional<String> optIdList) {
        PrezzoDTO retVal = new PrezzoDTO();

        String idList = optIdList.orElseGet(config::getListino);

        log.info("Listino di Riferimento: " + idList);

        DettListiniDTO prezzo = prezziService.selPrezzo(codArt, idList);

        if (prezzo != null) {
            log.info("Prezzo Articolo: " + prezzo.getPrezzo());

            double sconto = config.getSconto();
            int tipo = config.getTipo();

            retVal.setCodArt(codArt);
            retVal.setPrezzo(prezzo.getPrezzo());
            retVal.setSconto(sconto);
            retVal.setTipo(tipo);
        } else {
            log.warn(PREZZO_ARTICOLO_ASSENTE);
            return new ResponseEntity<>(retVal, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(retVal, HttpStatus.OK);
    }

    // ------------------- DELETE PREZZO LISTINO ------------------------------------
    @DeleteMapping(value = "/elimina/{codart}/{idlist}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> deletePrice(@PathVariable("codart") String codArt, @PathVariable("idlist") String idList) {
        log.info(String.format("Eliminazione prezzo listino %s dell'articolo %s", idList, codArt));

        prezziService.delPrezzo(codArt, idList);

        log.info("Eliminazione Prezzo Eseguita Con Successo");

        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message("Eliminazione Prezzo Eseguita Con Successo")
                .build(), HttpStatus.OK);
    }
}
