package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.model.Utenti;
import com.xantrix.webapp.service.UtentiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/utenti")
@Slf4j
public class UtentiController {

    public static final String UTENTE_NON_TROVATO = "L'utente %s non è stato trovato!";
    public static final String INSERIMENTO_UTENTE_ESEGUITO_CON_SUCCESSO = "Inserimento Utente %s Eseguito Con Successo";
    public static final String MODIFICA_UTENTE_ESEGUITO_CON_SUCCESSO = "Modifica Utente %s Eseguito Con Successo";
    public static final String UTENTE_NON_PRESENTE_IN_ANAGRAFICA = "Utente %s non presente in anagrafica!";
    @Autowired
    UtentiService utentiService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ResourceBundleMessageSource errMessage;

    @GetMapping(value = "/cerca/tutti")
    public List<Utenti> getAllUser() {
        log.info("Otteniamo tutti gli utenti");
        return utentiService.selTutti();
    }

    @GetMapping(value = "/cerca/userid/{userId}")
    public Utenti getUtente(@PathVariable("userId") String userId)
            throws NotFoundException {
        log.info("Otteniamo l'utente {}", userId);

        Utenti retVal = utentiService.selUser(userId);

        if (retVal == null) {
            String errMsg = String.format(UTENTE_NON_TROVATO, userId);
            log.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return retVal;
    }

    // ------------------- INSERIMENTO/MODIFICA UTENTE ------------------------------------
    @PostMapping(value = "/inserisci")
    public ResponseEntity<InfoMsg> addNewUser(@Valid @RequestBody Utenti utente,
                                        BindingResult bindingResult) throws BindingException {
        Utenti checkUtente = utentiService.selUser(utente.getUserId());

        if (checkUtente != null) {
            log.info("Modifica Utente");
            utente.setId(checkUtente.getId());
        } else {
            log.info("Inserimento Nuovo Utente");
        }

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }

        String encodedPassword = passwordEncoder.encode(utente.getPassword());
        utente.setPassword(encodedPassword);
        utentiService.save(utente);

        return new ResponseEntity<>(new InfoMsg(LocalDate.now(), HttpStatus.OK.toString(),
                String.format(checkUtente != null ? MODIFICA_UTENTE_ESEGUITO_CON_SUCCESSO : INSERIMENTO_UTENTE_ESEGUITO_CON_SUCCESSO,
                        utente.getUserId())), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE UTENTE ------------------------------------
    @DeleteMapping(value = "/elimina/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String userId) throws NotFoundException {
        log.info("Eliminiamo l'utente con id {}", userId);

        Utenti utente = utentiService.selUser(userId);

        if (utente == null) {
            String msgErr = String.format(UTENTE_NON_PRESENTE_IN_ANAGRAFICA, userId);
            log.warn(msgErr);
            throw new NotFoundException(msgErr);
        }

        utentiService.delete(utente);

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", String.format("Eliminazione Utente %s Eseguita Con Successo", userId));

        return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
    }
}
