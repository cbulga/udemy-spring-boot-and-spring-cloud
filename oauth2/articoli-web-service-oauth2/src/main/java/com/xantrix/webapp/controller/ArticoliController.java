package com.xantrix.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.ErrorResponse;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ArticoliService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/articoli")
@Slf4j
@Tag(name = "ArticoliController", description = "Controller Operazioni di Gestione Dati Articoli")
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

    private final PriceClient priceClient;

    public ArticoliController(ArticoliService articoliService, ResourceBundleMessageSource errMessage, PriceClient priceClient) {
        this.articoliService = articoliService;
        this.errMessage = errMessage;
        this.priceClient = priceClient;
    }

    @Operation(summary = "Ricerca l'articolo per BARCODE",
            description = "Restituisce i dati dell'articolo in formato JSON",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'articolo cercato è stato trovato!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Articoli.class))),
            @ApiResponse(responseCode = "401", description = "Utente non AUTENTICATO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Utente Non AUTORIZZATO ad accedere alle informazioni", content = @Content),
            @ApiResponse(responseCode = "404", description = "L'articolo cercato NON è stato trovato!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(value = {"/cerca/ean/{barCode}", "/cerca/ean/{barCode}/{idlist}"}, produces = "application/json")
    // ---------------------- Ricerca per barcode -----------------------------
    public ResponseEntity<ArticoliDto> listArtByEan(@Parameter(description = "Barcode Articolo", required = true) @PathVariable("barCode") String barCode,
                                                    @PathVariable("idlist") Optional<String> optIdList,
                                                    HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Otteniamo l'articolo con barcode {} ******", barCode);
        ArticoliDto articolo = articoliService.selByBarCode(barCode);

        if (articolo == null) {
            String errorMessage = String.format(BARCODE_NOT_FOUND, barCode);
            log.warn(errorMessage, barCode);
            throw new NotFoundException(errorMessage);
        } else {
            String authHeader = httpServletRequest.getHeader("Authorization");
            articolo.setPrezzo(getPriceArt(authHeader, articolo.getCodArt(), optIdList.orElse(null)));
        }

        return new ResponseEntity<>(articolo, HttpStatus.OK);
    }

    // ------------------- Ricerca Per Codice ------------------------------------
    @Operation(summary = "Ricerca l'articolo per CODICE",
            description = "Restituisce i dati dell'articolo in formato JSON",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'articolo cercato è stato trovato!", content = @Content(schema = @Schema(implementation = Articoli.class))),
            @ApiResponse(responseCode = "401", description = "Utente non AUTENTICATO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Utente Non AUTORIZZATO ad accedere alle informazioni", content = @Content),
            @ApiResponse(responseCode = "404", description = "L'articolo cercato NON è stato trovato!", content = @Content)})
    @GetMapping(value = {"/cerca/codice/{codArt}", "/cerca/codice/{codArt}/{idlist}"}, produces = "application/json")
    public ResponseEntity<ArticoliDto> listArtByCodArt(@Parameter(description = "Codice univoco dell'articolo", required = true) @PathVariable("codArt") String codArt,
                                                       @PathVariable("idlist") Optional<String> optIdList,
                                                       HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Otteniamo l'articolo con codice {} ******", codArt);
        ArticoliDto articolo = articoliService.selByCodArt(codArt);

        if (articolo == null) {
            String errorMessage = String.format(ARTICOLO_NON_TROVATO, codArt);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            String authHeader = httpServletRequest.getHeader("Authorization");
            articolo.setPrezzo(getPriceArt(authHeader, codArt, optIdList.orElse(null)));
        }

        return new ResponseEntity<>(articolo, HttpStatus.OK);
    }

    // ------------------- Ricerca Per Descrizione ------------------------------------
    @Operation(summary = "Ricerca uno o più articoli per descrizione o parte",
            description = "Restituisce i dati dell'articolo in formato JSON",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'articolo/i cercato/i sono stati trovati!", content = @Content(schema = @Schema(implementation = Articoli.class))),
            @ApiResponse(responseCode = "401", description = "Utente non AUTENTICATO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Utente Non AUTORIZZATO ad accedere alle informazioni", content = @Content),
            @ApiResponse(responseCode = "404", description = "L'articolo/i cercato/i NON sono stati trovati!", content = @Content)})
    @GetMapping(value = {"/cerca/descrizione/{descrizione}", "/cerca/descrizione/{descrizione}/{idlist}"}, produces = "application/json")
    public ResponseEntity<List<ArticoliDto>> listArtByDescrizione(@Parameter(description = "Descrizione dell'articolo", required = true) @PathVariable("descrizione") String descrizione,
                                                                  @PathVariable("idlist") Optional<String> optIdList,
                                                                  HttpServletRequest httpServletRequest) throws NotFoundException {
        log.info("****** Otteniamo gli articoli con descrizione {} ******", descrizione);
        List<ArticoliDto> articoli = articoliService.selByDescrizione(descrizione + "%");
        if (articoli.isEmpty()) {
            String errorMessage = String.format(ARTICOLO_PER_DESCRIZIONE_NON_TROVATO, descrizione);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            String authHeader = httpServletRequest.getHeader("Authorization");
            articoli.forEach(a -> a.setPrezzo(getPriceArt(authHeader, a.getCodArt(), optIdList.orElse(null))));
        }

        return new ResponseEntity<>(articoli, HttpStatus.OK);
    }

    // ------------------- INSERIMENTO ARTICOLO ------------------------------------
    @Operation(
            summary = "Inserimento dati NUOVO articolo",
            description = "Può essere usato solo per l'inserimento dati di un nuovo articolo",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Dati articolo salvati con successo"),
                    @ApiResponse(responseCode = "400", description = "Uno o più dati articolo non validi"),
                    @ApiResponse(responseCode = "406", description = "Inserimento dati articolo esistente in anagrafica"),
                    @ApiResponse(responseCode = "403", description = "Non sei AUTORIZZATO ad inserire dati"),
                    @ApiResponse(responseCode = "401", description = "Non sei AUTENTICATO")
            })
    @PostMapping(value = "/inserisci", produces = "application/json")
    public ResponseEntity<?> createArt(@Parameter(description = "JSON che rappresenta l'articolo", required = true) @Valid @RequestBody Articoli articoli, BindingResult bindingResult) throws DuplicateException, BindingException {
        log.info("****** Salviamo l'articolo con codice {} ******", articoli.getCodArt());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }
        log.debug("Verifichiamo la presenza dell'articolo passato");
        ArticoliDto duplicatedArticoli = articoliService.selByCodArt(articoli.getCodArt());
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
    @Operation(
            summary = "MODIFICA dati articolo in anagrfica",
            description = "Può essere usato solo per la modifica dati di un articolo presente in anagrafica",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Dati articolo salvati con successo"),
                    @ApiResponse(responseCode = "400", description = "Uno o più dati articolo non validi"),
                    @ApiResponse(responseCode = "404", description = "Articolo non presente in anagrafica"),
                    @ApiResponse(responseCode = "403", description = "Non sei AUTORIZZATO ad inserire dati"),
                    @ApiResponse(responseCode = "401", description = "Non sei AUTENTICATO")
            })
    @PutMapping(value = "/modifica", produces = "application/json")
    public ResponseEntity<?> updateArt(@Parameter(description = "JSON che rappresenta l'articolo", required = true) @Valid @RequestBody Articoli articoli, BindingResult bindingResult) throws NotFoundException, BindingException {
        log.info("****** Modifichiamo l'articolo con codice {} ******", articoli.getCodArt());

        if (bindingResult.hasErrors()) {
            String msgErr = errMessage.getMessage(Objects.requireNonNull(bindingResult.getFieldError()), LocaleContextHolder.getLocale());
            log.warn(msgErr);
            throw new BindingException(msgErr);
        }

        ArticoliDto articoliToUpdate = articoliService.selByCodArt(articoli.getCodArt());
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

    @Operation(
            summary = "ELIMINAZIONE dati articolo in anagrfica",
            description = "Si esegue una eliminazione a cascata dei barcode e degli ingredienti",
            tags = {"Articoli"},
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value =
            {@ApiResponse(responseCode = "200", description = "Dati articolo eliminati con successo"),
                    @ApiResponse(responseCode = "404", description = "Articolo non presente in anagrafica"),
                    @ApiResponse(responseCode = "403", description = "Non sei AUTORIZZATO ad inserire dati"),
                    @ApiResponse(responseCode = "401", description = "Non sei AUTENTICATO")
            })
    @DeleteMapping(value = "/elimina/{codArt}", produces = "application/json")
    public ResponseEntity<?> deleteArt(@Parameter(description = "Codice univoco dell'articolo", required = true) @PathVariable("codArt") String codArt) throws NotFoundException {
        log.info("****** Eliminiamo l'articolo con codice {} ******", codArt);

        Articoli articoliToDelete = articoliService.selByCodArt2(codArt);
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

    protected double getPriceArt(String authHeader, String codArt, String idList) {
        double prezzo = StringUtils.isNotEmpty(idList)
                ? priceClient.getPriceArt(authHeader, codArt, idList)
                : priceClient.getDefPriceArt(authHeader, codArt);

        log.info("Prezzo articolo {}: {}", codArt, prezzo);
        return prezzo;
    }
}
