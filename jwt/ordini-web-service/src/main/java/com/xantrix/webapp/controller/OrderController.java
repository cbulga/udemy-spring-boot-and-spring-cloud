package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.InfoMsg;
import com.xantrix.webapp.dto.OrderDTO;
import com.xantrix.webapp.entity.Ordini;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.OrderMessagingService;
import com.xantrix.webapp.service.OrdiniService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/ordini")
@Slf4j
public class OrderController {

    public static final String ORDER_NOT_FOUND = "L'ordine %s non è stato trovato!";
    private final OrderMessagingService orderMessagingService;
    private final OrdiniService ordiniService;
    private final ResourceBundleMessageSource errMessage;

    public OrderController(OrderMessagingService orderMessagingService, OrdiniService ordiniService, ResourceBundleMessageSource errMessage) {
        this.orderMessagingService = orderMessagingService;
        this.ordiniService = ordiniService;
        this.errMessage = errMessage;
    }

    @PostMapping(value = {"/invia/{ordineId}", "/invia/{ordineId}/{fonte}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> sendOrder(@PathVariable("ordineId") String ordineId,
                                             @PathVariable("fonte") Optional<String> fonte) throws NotFoundException {
        String calculatedFonte = (fonte.isEmpty()) ? "Web" : fonte.get();

        OrderDTO order = new OrderDTO();
        Ordini ordini = ordiniService.findById(ordineId)
                .orElseThrow(() -> new NotFoundException(String.format(ORDER_NOT_FOUND, ordineId)));

        order.setId(ordini.getId());
        order.setIdCliente(ordini.getCodfid());
        order.setData(ordini.getData());
        order.setTotale(ordiniService.selValTot(ordineId));
        order.setFonte(calculatedFonte);

        orderMessagingService.sendOrder(order);

        return ResponseEntity.ok(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message(String.format("Inviato Ordine (%s) al gestore dei bollini", order.getId()))
                .build());
    }

    //FIXME: use OrderDTO!!!
    // ------------------- Ricerca Per Codice ------------------------------------
    @GetMapping(value = "/cerca/codice/{ordineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Ordini> listOrdByCode(@PathVariable("ordineId") String ordineId) throws NotFoundException {
        log.info("****** Otteniamo l'ordini con codice {} *******", ordineId);

        //String AuthHeader = httpRequest.getHeader("Authorization");
        Ordini ordini = ordiniService.findById(ordineId)
                .orElseThrow(() -> new NotFoundException(String.format(ORDER_NOT_FOUND, ordineId)));

        return ResponseEntity.ok(ordini);
    }

    //FIXME: use OrderDTO!!!
    // ------------------- INSERIMENTO ORDINE ------------------------------------
    @PostMapping(value = "/inserisci", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> createOrd(@Valid @RequestBody Ordini ordine, BindingResult bindingResult) throws BindingException {
        log.info("Salviamo l'ordine con id " + ordine.getId());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }

        if (StringUtils.isBlank(ordine.getId())) {
            UUID uuid = UUID.randomUUID();
            ordine.setId(uuid.toString());
        }

        ordiniService.insOrdine(ordine);

        return new ResponseEntity<>(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message(ordine.getId())
                .build(), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE ORDINE ------------------------------------
    @DeleteMapping(value = "/elimina/{ordineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InfoMsg> deleteOrd(@PathVariable("ordineId") String ordineId) throws NotFoundException {
        log.info("Eliminiamo l'ordine con id {}", ordineId);

        Ordini ordine = ordiniService.findById(ordineId)
                .orElseThrow(() -> new NotFoundException(String.format(ORDER_NOT_FOUND, ordineId)));

        ordiniService.delOrdine(ordine);

        return ResponseEntity.ok(InfoMsg.builder()
                .date(LocalDate.now())
                .code(HttpStatus.OK.toString())
                .message(String.format("Eliminazione Ordine %s Eseguita Con Successo", ordineId))
                .build());
    }
}
