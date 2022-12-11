package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.ClientiDTO;
import com.xantrix.webapp.dto.InfoMsg;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ClientiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/clienti")
@Slf4j
public class ClientiController {

    public static final String MODIFICA_BOLLINI_CLIENTE_ESEGUITA_CON_SUCCESSO = "Modifica dei bollini a %s per il cliente %s eseguita con successo";
    private final ClientiService clientiService;

    public ClientiController(ClientiService clientiService) {
        this.clientiService = clientiService;
    }

    @GetMapping(value = "/cerca/codice/{codice}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientiDTO> getClientiById(@PathVariable("codice") String codice) throws NotFoundException {
        log.info("****** Restituiamo il cliente con codice {} ******", codice);
        ClientiDTO result = clientiService.findByCodice(codice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value = "/cards/modifica/{codice}/{bollini}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> updateBolliniByCodice(@PathVariable("codice") String codice, @PathVariable("bollini") Integer bollini) throws NotFoundException {
        log.info("****** Aggiorniamo i bollini a {bollini} per il cliente con codice {} ******", bollini, codice);
        clientiService.updateBolliniByCodice(bollini, codice);
        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message(String.format(MODIFICA_BOLLINI_CLIENTE_ESEGUITA_CON_SUCCESSO, bollini, codice))
                .build(), HttpStatus.OK);
    }
}
