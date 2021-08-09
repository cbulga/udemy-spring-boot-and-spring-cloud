package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ArticoliService;
import io.swagger.annotations.*;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/articoli")
@Slf4j
@Api(value = "Alphashop", tags = "Controller Operazioni di gestione dati articoli")
public class ArticoliController {

    public static final String BARCODE_NOT_FOUND = "Il barcode %s non è stato trovato!";
    public static final String ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA_IMPOSSIBILE_UTILIZZARE_IL_METODO_PUT = "Articolo %s non presente in anagrafica! Impossibile utilizzare il metodo PUT";
    public static final String MODIFICA_ARTICOLO_ESEGUITA_CON_SUCCESSO = "Modifica Articolo %s Eseguita Con Successo";
    public static final String ELIMINAZIONE_ARTICOLO_ESEGUITA_CON_SUCCESSO = "Eliminazione Articolo %s Eseguita Con Successo";
    public static final String ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA = "Articolo %s non presente in anagrafica!";
    public static final String ARTICOLO_NON_TROVATO = "L'articolo con codice %s non è stato trovato!";
    public static final String ARTICOLO_PER_DESCRIZIONE_NON_TROVATO = "Non è stato trovato alcun articolo avente descrizione %s";
    public static final String ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST = "Articolo %s presente in anagrafica! Impossibile utilizzare il metodo POST";
    public static final String INSERIMENTO_ARTICOLO_ESEGUITO_CON_SUCCESSO = "Inserimento Articolo %s Eseguito Con Successo";

    private final ArticoliService articoliService;

    private final ResourceBundleMessageSource errMessage;

    public ArticoliController(ArticoliService articoliService, ResourceBundleMessageSource errMessage) {
        this.articoliService = articoliService;
        this.errMessage = errMessage;
    }

    @ApiOperation(
		      value = "Ricerca l'articolo per BARCODE", 
		      notes = "Restituisce i dati dell'articolo in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "L'articolo cercato è stato trovato!"),
	    @ApiResponse(code = 404, message = "L'articolo cercato NON è stato trovato!"),
	    @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
	    @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @GetMapping(value = "/cerca/ean/{barCode}", produces = "application/json")
    // ---------------------- Ricerca per barcode -----------------------------
    public ResponseEntity<Articoli> listArtByEan(@ApiParam("Barcode univoco dell'articolo") @PathVariable("barCode") String barCode) throws NotFoundException {
        log.info("****** Otteniamo l'articolo con barcode {} ******", barCode);
        Articoli articolo = articoliService.selByBarCode(barCode);

        if (articolo == null) {
            String errorMessage = String.format(BARCODE_NOT_FOUND, barCode);
            log.warn(errorMessage, barCode);
            throw new NotFoundException(errorMessage);
        }

        return new ResponseEntity<>(articolo, HttpStatus.OK);
    }

	// ------------------- Ricerca Per Codice ------------------------------------
	@ApiOperation(
		      value = "Ricerca l'articolo per CODICE", 
		      notes = "Restituisce i dati dell'articolo in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{ 	@ApiResponse(code = 200, message = "L'articolo cercato è stato trovato!"),
		@ApiResponse(code = 404, message = "L'articolo cercato NON è stato trovato!"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @GetMapping(value = "/cerca/codice/{codArt}", produces = "application/json")
    public ResponseEntity<Articoli> listArtByCodArt(@ApiParam("Codice univoco dell'articolo") @PathVariable("codArt") String codArt) throws NotFoundException {
        log.info("****** Otteniamo l'articolo con codice {} ******", codArt);
        Articoli articolo = articoliService.selByCodArt(codArt);

        if (articolo == null) {
            String errorMessage = String.format(ARTICOLO_NON_TROVATO, codArt);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return new ResponseEntity<>(articolo, HttpStatus.OK);
    }

	// ------------------- Ricerca Per Descrizione ------------------------------------
	@ApiOperation(
		      value = "Ricerca l'articolo per DESCRIZIONE o parte di essa", 
		      notes = "Restituisce un collezione di articoli in formato JSON",
		      response = Articoli.class, 
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "L'articolo/i sono stati trovati"),
		@ApiResponse(code = 404, message = "Non è stato trovato alcun articolo"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @GetMapping(value = "/cerca/descrizione/{descrizione}", produces = "application/json")
    public ResponseEntity<List<Articoli>> listArtByDescrizione(@ApiParam("Descrizione dell'articolo") @PathVariable("descrizione") String descrizione) throws NotFoundException {
        log.info("****** Otteniamo gli articoli con descrizione {} ******", descrizione);
        List<Articoli> articoli = articoliService.selByDescrizione(descrizione + "%");
        if (articoli.isEmpty()) {
            String errorMessage = String.format(ARTICOLO_PER_DESCRIZIONE_NON_TROVATO, descrizione);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return new ResponseEntity<>(articoli, HttpStatus.OK);
    }

	// ------------------- INSERIMENTO ARTICOLO ------------------------------------
	@ApiOperation(
		      value = "Inserimento dati NUOVO articolo", 
		      notes = "Può essere usato solo per l'inserimento dati di un nuovo articolo",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo salvati con successo"),
		@ApiResponse(code = 400, message = "Uno o più dati articolo non validi"),
		@ApiResponse(code = 406, message = "Inserimento dati articolo esistente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @PostMapping(value = "/inserisci", produces = "application/json")
    public ResponseEntity<?> createArt(@ApiParam("JSON che rappresenta l'articolo") @Valid @RequestBody Articoli articoli, BindingResult bindingResult) throws DuplicateException, BindingException {
        log.info("****** Salviamo l'articolo con codice {} ******", articoli.getCodArt());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }
        log.debug("Verifichiamo la presenza dell'articolo passato");
        Articoli duplicatedArticoli = articoliService.selByCodArt(articoli.getCodArt());
        if (duplicatedArticoli != null) {
            String errorMessage = String.format(ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST, articoli.getCodArt());
            log.warn(errorMessage);
            throw new DuplicateException(errorMessage);
        }

        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();
        headers.setContentType(MediaType.APPLICATION_JSON);

        articoliService.insArticolo(articoli);

        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", String.format(INSERIMENTO_ARTICOLO_ESEGUITO_CON_SUCCESSO, articoli.getCodArt()));

        return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
    }

	// ------------------- MODIFICA ARTICOLO ------------------------------------
	@ApiOperation(
		      value = "MODIFICA dati articolo in anagrfica", 
		      notes = "Può essere usato solo per la modifica dati di un articolo presente in anagrafica",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo salvati con successo"),
		@ApiResponse(code = 400, message = "Uno o più dati articolo non validi"),
		@ApiResponse(code = 404, message = "Articolo non presente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @PutMapping(value = "/modifica", produces = "application/json")
    public ResponseEntity<?> updateArt(@ApiParam("JSON che rappresenta l'articolo") @Valid @RequestBody Articoli articoli, BindingResult bindingResult) throws NotFoundException, BindingException {
        log.info("****** Modifichiamo l'articolo con codice {} ******", articoli.getCodArt());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }

        Articoli articoliToUpdate = articoliService.selByCodArt(articoli.getCodArt());
        if (articoliToUpdate == null) {
            String errorMessage = String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA_IMPOSSIBILE_UTILIZZARE_IL_METODO_PUT, articoli.getCodArt());
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        articoliService.insArticolo(articoli);

        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode result = objectMapper.createObjectNode();
        result.put("code", HttpStatus.OK.toString());
        result.put("message", String.format(MODIFICA_ARTICOLO_ESEGUITA_CON_SUCCESSO, articoli.getCodArt()));

        return new ResponseEntity<>(result, headers, HttpStatus.CREATED);
    }

	@ApiOperation(
		      value = "ELIMINAZIONE dati articolo in anagrfica", 
		      notes = "Si esegue una eliminazione a cascata dei barcode e degli ingredienti",
		      produces = "application/json")
	@ApiResponses(value =
	{   @ApiResponse(code = 200, message = "Dati articolo eliminati con successo"),
		@ApiResponse(code = 404, message = "Articolo non presente in anagrafica"),
		@ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad inserire dati"),
		@ApiResponse(code = 401, message = "Non sei AUTENTICATO")
	})
    @DeleteMapping(value = "/elimina/{codArt}", produces = "application/json")
    public ResponseEntity<?> deleteArt(@ApiParam("Codice univoco dell'articolo") @PathVariable("codArt") String codArt) throws NotFoundException {
        log.info("****** Eliminiamo l'articolo con codice {} ******", codArt);

        Articoli articoliToDelete = articoliService.selByCodArt(codArt);
        if (articoliToDelete == null) {
            String errorMessage = String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA, codArt);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        articoliService.delArticolo(articoliToDelete);

        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode result = objectMapper.createObjectNode();
        result.put("code", HttpStatus.OK.toString());
        result.put("message", String.format(ELIMINAZIONE_ARTICOLO_ESEGUITA_CON_SUCCESSO, codArt));
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}
