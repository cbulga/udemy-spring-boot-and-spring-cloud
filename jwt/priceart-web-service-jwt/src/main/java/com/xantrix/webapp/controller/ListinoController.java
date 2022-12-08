package com.xantrix.webapp.controller;

import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.dtos.ListiniDTO;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ListinoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/listino")
@Slf4j
public class ListinoController {

    private final ListinoService listiniService;
    private final ResourceBundleMessageSource errMessage;

    public ListinoController(ListinoService listiniService, ResourceBundleMessageSource errMessage) {
        this.listiniService = listiniService;
        this.errMessage = errMessage;
    }

    // ------------------- CERCA LISTINO X ID ------------------------------------
    @GetMapping(value = "/cerca/id/{idList}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Listini>> getListById(@PathVariable("idList") String idList) throws NotFoundException {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("Otteniamo il Listino Numero: {}", idList);

        Optional<Listini> listini = listiniService.selById(idList);

        if (listini.isEmpty()) {
            String errMsg = String.format("Il listino %s non è stato trovato!", idList);
            log.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new ResponseEntity<>(listini, headers, HttpStatus.OK);
    }

    // ------------------- INSERISCI LISTINO ------------------------------------
    @PostMapping(value = "/inserisci", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> createList(@Valid @RequestBody ListiniDTO listiniDto, BindingResult bindingResult) throws BindingException {
        log.info("Salviamo il listino {}", listiniDto.getId());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }

        listiniService.insListino(listiniDto);

        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message("Inserimento Listino " + listiniDto.getId() + " Eseguito Con Successo")
                .build(), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE LISTINO ------------------------------------
    @DeleteMapping(value = "/elimina/{idList}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> deleteList(@PathVariable("idList") String idList)
            throws NotFoundException {
        log.info("Eliminiamo il listino {}", idList);

        listiniService.delListino(idList);

        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message("Eliminazione Listino " + idList + " Eseguita Con Successo")
                .build(), HttpStatus.OK);
    }
}
