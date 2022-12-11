package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.ClientiDTO;
import com.xantrix.webapp.dto.CreateClientiDTO;
import com.xantrix.webapp.dto.InfoMsg;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ClientiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequestMapping("/api/clienti")
@Slf4j
public class ClientiController {

    public static final String MODIFICA_BOLLINI_CLIENTE_ESEGUITA_CON_SUCCESSO = "Modifica dei bollini a %s per il cliente %s eseguita con successo";
    private final ClientiService clientiService;
    private final ResourceBundleMessageSource errMessage;

    public ClientiController(ClientiService clientiService, ResourceBundleMessageSource errMessage) {
        this.clientiService = clientiService;
        this.errMessage = errMessage;
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

    @PostMapping(value = "/inserisci", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientiDTO> createClienti(@Valid @RequestBody CreateClientiDTO createClientiDTO, BindingResult bindingResult) throws BindingException, DuplicateException {
        log.info("****** Inseriamo il cliente con codice {} ******", createClientiDTO.getCodice());
        handleBindingResult(bindingResult);
        ClientiDTO result = clientiService.createClienti(createClientiDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private void handleBindingResult(BindingResult bindingResult) throws BindingException {
        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }
    }
}
