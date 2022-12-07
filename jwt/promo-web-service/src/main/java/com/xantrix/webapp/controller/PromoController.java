package com.xantrix.webapp.controller;

import com.xantrix.webapp.dto.CreatePromoDTO;
import com.xantrix.webapp.dto.PromoDTO;
import com.xantrix.webapp.dto.UpdatePromoDTO;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/promo")
@Slf4j
public class PromoController {

    public static final String AUTHORIZATION = "Authorization";
    private final PromoService promoService;
    private final ResourceBundleMessageSource errMessage;

    public PromoController(PromoService promoService, ResourceBundleMessageSource errMessage) {
        this.promoService = promoService;
        this.errMessage = errMessage;
    }

    @GetMapping(value = "/id/{promoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromoDTO> getPromoById(@PathVariable("promoId") String promoId,
                                                 HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Restituiamo la promo con ID {} ******", promoId);
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        PromoDTO result = promoService.findById(promoId, authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/codice", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromoDTO> getPromoByAnnoAndCodice(@RequestParam(value = "anno") Integer anno,
                                                            @RequestParam(value = "codice") String codice,
                                                            HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Restituiamo la promo con anno {} e codice {} ******", anno, codice);
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        PromoDTO result = promoService.findByAnnoAndCodice(anno, codice, authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PromoDTO>> getAllPromos(HttpServletRequest httpServletRequest) {
        log.info("****** Restituiamo tutte le promozioni ******");
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        List<PromoDTO> result = promoService.findAllOrderByIdPromo(authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PromoDTO>> getActivePromos(HttpServletRequest httpServletRequest) {
        log.info("****** Restituiamo tutte le promozioni attive ******");
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        List<PromoDTO> result = promoService.findByActive(authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/elimina/{idPromo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromoDTO> deleteByIdPromo(@PathVariable(value = "idPromo") String idPromo,
                                                    HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Cancelliamo la promo con ID {} ******", idPromo);
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        PromoDTO result = promoService.deleteByIdPromo(idPromo, authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/elimina/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PromoDTO>> deleteAll(HttpServletRequest httpServletRequest) {
        log.info("****** Cancelliamo tutte le promo ******");
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        List<PromoDTO> result = promoService.deleteAll(authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/inserisci", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromoDTO> createPromo(@Valid @RequestBody CreatePromoDTO promoDto, BindingResult bindingResult,
                                                HttpServletRequest httpServletRequest) throws BindingException, DuplicateException, NotFoundException {
        log.info("****** Inseriamo la promo con codice {} ******", promoDto.getCodice());
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        handleBindingResult(bindingResult);
        PromoDTO result = promoService.createPromo(promoDto, authHeader);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping(value = "/aggiorna", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromoDTO> updatePromo(@Valid @RequestBody UpdatePromoDTO promoDto, BindingResult bindingResult,
                                                HttpServletRequest httpServletRequest) throws BindingException, NotFoundException {
        log.info("****** Aggiorniamo la promo con codice {} ******", promoDto.getCodice());
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION);
        handleBindingResult(bindingResult);
        PromoDTO result = promoService.updatePromo(promoDto, authHeader);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/clearAllCaches")
    public void clearAllCaches() {
        log.info("************** Clearing all caches *************");
        promoService.cleanCaches();
    }

    private void handleBindingResult(BindingResult bindingResult) throws BindingException {
        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }
    }
}
