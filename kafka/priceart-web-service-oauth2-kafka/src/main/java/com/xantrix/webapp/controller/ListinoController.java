package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ListinoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/listino")
public class ListinoController {

    private static final Logger logger = LoggerFactory.getLogger(ListinoController.class);
    private final ListinoService listiniService;
    private final ResourceBundleMessageSource errMessage;

    public ListinoController(ListinoService listiniService, ResourceBundleMessageSource errMessage) {
        this.listiniService = listiniService;
        this.errMessage = errMessage;
    }

    // ------------------- CERCA LISTINO X ID ------------------------------------
    @GetMapping(value = "/cerca/id/{idList}")
    public ResponseEntity<Optional<Listini>> getListById(@PathVariable("idList") String IdList) throws NotFoundException {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        logger.info("Otteniamo il Listino Numero: " + IdList);

        Optional<Listini> listini = listiniService.selById(IdList);

        if (!listini.isPresent()) {
            String errMsg = String.format("Il listino %s non è stato trovato!", IdList);
            logger.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new ResponseEntity<>(listini, headers, HttpStatus.OK);
    }

    // ------------------- INSERISCI LISTINO ------------------------------------
    @RequestMapping(value = "/inserisci", method = RequestMethod.POST)
    public ResponseEntity<ObjectNode> createList(@Valid @RequestBody Listini listino, BindingResult bindingResult,
                                        UriComponentsBuilder ucBuilder) throws BindingException {
        logger.info(String.format("Salviamo il listino", listino.getId()));

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
            logger.warn(msgErr);
            throw new BindingException(msgErr);
        }

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode responseNode = mapper.createObjectNode();

        listiniService.insListino(listino);

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Inserimento Listino " + listino.getId() + " Eseguito Con Successo");

        return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE LISTINO ------------------------------------
    @RequestMapping(value = "/elimina/{idList}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<ObjectNode> deleteList(@PathVariable("idList") String idList)
            throws NotFoundException {
        logger.info("Eliminiamo il listino {}", idList);

        Optional<Listini> listino = listiniService.selById(idList);

        if (!listino.isPresent()) {
            String msgErr = String.format("Listino %s non presente in anagrafica!", idList);
            logger.warn(msgErr);
            throw new NotFoundException(msgErr);
        }

        listiniService.delListino(listino.get());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Listino " + idList + " Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.OK);
    }
}
