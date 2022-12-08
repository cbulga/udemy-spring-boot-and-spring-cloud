package com.xantrix.webapp.service;

import com.xantrix.webapp.config.Resilience4JConfiguration;
import com.xantrix.webapp.dto.*;
import com.xantrix.webapp.entity.DepRifPromo;
import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.entity.Promo;
import com.xantrix.webapp.entity.TipoPromo;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.feign.ArticoliClient;
import com.xantrix.webapp.repository.PromoRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PromoServiceImpl implements PromoService {

    public static final String PROMO_DUPLICATO = "Promo %s presente in anagrafica!";
    static final String RETURN_VALUE = "return value is {}";
    public static final String VERIFICHIAMO_LA_PRESENZA_DELLA_PROMO_PASSATA = "Verifichiamo la presenza della promo passata";
    public static final String NOT_FOUND_PROMO_ID_PROMO = "NotFound.Promo.idPromo";
    private final CacheManager cacheManager;
    private final PromoRepository promoRepository;
    private final ResourceBundleMessageSource errMessage;
    private final TipoPromoService tipoPromoService;
    private final ArticoliClient articoliClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final ModelMapper modelMapper;

    public PromoServiceImpl(CacheManager cacheManager, PromoRepository promoRepository, ResourceBundleMessageSource errMessage,
                            TipoPromoService tipoPromoService, ArticoliClient articoliClient,
                            CircuitBreakerFactory<?, ?> circuitBreakerFactory, ModelMapper modelMapper) {
        this.cacheManager = cacheManager;
        this.promoRepository = promoRepository;
        this.errMessage = errMessage;
        this.tipoPromoService = tipoPromoService;
        this.articoliClient = articoliClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.modelMapper = modelMapper;
    }

    @Override
    public void cleanCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            log.debug("Evicting cache {}", cacheName);
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    @CacheEvict(cacheNames = {"promos", "promoByIdPromo", "promoByCodiceAndAnno", "promosActive", "prezziPromosByCodArtAndIsFidAndCodFid"}, allEntries = true)
    public PromoDTO createPromo(CreatePromoDTO promoDto, String authHeader) throws DuplicateException, NotFoundException {
        log.debug("createPromo(promoDto={})", promoDto);
        PromoDTO result;
        log.debug(VERIFICHIAMO_LA_PRESENZA_DELLA_PROMO_PASSATA);
        if (promoRepository.findByCodice(promoDto.getCodice()).isPresent()) {
            String errorMessage = String.format(PROMO_DUPLICATO, promoDto.getCodice());
            log.warn(errorMessage);
            throw new DuplicateException(errorMessage);
        }
        List<String> notFoundTipoPromos = new ArrayList<>();
        Promo promo = Promo.builder()
                .anno(promoDto.getAnno())
                .codice(promoDto.getCodice().trim())
                .descrizione(promoDto.getDescrizione().trim())
                .build();
        Promo finalPromo = promo;
        promoDto.getDepRifPromos().forEach(dto -> {
            DepRifPromo depRifPromo = new DepRifPromo();
            finalPromo.getDepRifPromos().add(depRifPromo);
            updateDepRifPromo(depRifPromo, dto, finalPromo);
        });
        promoDto.getDettPromos().stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.nullsFirst(Comparator.comparing(IDettPromoDTO::getRiga)))
                .forEach(dto -> {
                    DettPromo dettPromo = new DettPromo();
                    finalPromo.getDettPromos().add(dettPromo);
                    notFoundTipoPromos.addAll(updateDettPromo(dettPromo, dto, finalPromo));
                });
        if (!notFoundTipoPromos.isEmpty())
            throw new NotFoundException(String.join(",", notFoundTipoPromos));
        promo = promoRepository.save(promo);
        result = createPromoDTO(promo, authHeader);
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable senza parametri avranno la cache svuotata
            @CacheEvict(cacheNames = "promos", allEntries = true),
            @CacheEvict(cacheNames = "promosActive", allEntries = true),
            @CacheEvict(cacheNames = "prezziPromosByCodArtAndIsFidAndCodFid", allEntries = true),
            // tutti i metodi che usano @Cacheable con key = "#promoDto.idPromo" avranno la cache svuotata per quella promo
//            @CacheEvict(cacheNames = "promoByIdPromo", key = "#promoDto.idPromo")
    }, put = {
            @CachePut(value = "promoByIdPromo", key = "#promoDto.idPromo"),
            @CachePut(value = "promoByCodiceAndAnno", key = "#promoDto.codice.concat('-').concat(#promoDto.anno.toString())")
    })
    public PromoDTO updatePromo(UpdatePromoDTO promoDto, String authHeader) throws NotFoundException {
        log.debug("updatePromo(promoDto={})", promoDto);
        PromoDTO result;
        log.debug(VERIFICHIAMO_LA_PRESENZA_DELLA_PROMO_PASSATA);
        Promo promo = promoRepository.findById(promoDto.getIdPromo())
                .orElseThrow(() -> new NotFoundException(errMessage.getMessage(NOT_FOUND_PROMO_ID_PROMO,
                        new String[]{promoDto.getIdPromo()}, LocaleContextHolder.getLocale())));
        // elimino dalla cache per anno a codice i valori attuali prima di aggiornarli
        evictSingleCacheValue("promoByIdPromo", promo.getIdPromo());
        evictSingleCacheValue("promoByCodiceAndAnno", promo.getCodice().concat("-").concat(promo.getAnno().toString()));

        promo.setAnno(promoDto.getAnno());
        promo.setCodice(promoDto.getCodice().trim());
        promo.setDescrizione(promoDto.getDescrizione().trim());

        // elimino i DepRifPromo e DettPromo non presenti nel DTO
        promo.getDepRifPromos().removeIf(depRifPromo -> promoDto.getDepRifPromos().stream()
                .noneMatch(depRifPromoDTO -> depRifPromoDTO.getId().equals(depRifPromo.getId())));
        promo.getDettPromos().removeIf(dettPromo -> promoDto.getDettPromos().stream()
                .noneMatch(dettPromoDTO -> dettPromoDTO.getId().equals(dettPromo.getId())));

        // aggiorno i DepRifPromo e DettPromo presenti sia nel DTO sia in Promo
        List<String> notFoundTipoPromos = new ArrayList<>();
        Promo finalPromo = promo;
        promoDto.getDepRifPromos().forEach(depRifPromoDTO -> finalPromo.getDepRifPromos().stream()
                .filter(depRifPromo -> depRifPromo.getId().equals(depRifPromoDTO.getId()))
                .findAny()
                .ifPresent(depRifPromo -> depRifPromo.setIdDeposito(depRifPromoDTO.getIdDeposito())));
        promoDto.getDettPromos().forEach(dettPromoDTO -> finalPromo.getDettPromos().stream()
                .filter(dettPromo -> dettPromo.getId().equals(dettPromoDTO.getId()))
                .findAny()
                .ifPresent(dettPromo -> notFoundTipoPromos.addAll(updateDettPromo(dettPromo, dettPromoDTO, finalPromo))));

        // aggiungo i DepRifPromo e DettPromo non presenti in Promo
        List<DepRifPromoDTO> newDepRifPromoDTOs = promoDto.getDepRifPromos().stream()
                .filter(depRifPromoDTO -> finalPromo.getDepRifPromos().stream()
                        .noneMatch(depRifPromo -> depRifPromo.getId().equals(depRifPromoDTO.getId())))
                .toList();
        newDepRifPromoDTOs.forEach(depRifPromoDTO -> {
            DepRifPromo depRifPromo = new DepRifPromo();
            finalPromo.getDepRifPromos().add(depRifPromo);
            updateDepRifPromo(depRifPromo, depRifPromoDTO, finalPromo);
        });
        List<CreateOrUpdateDettPromoDTO> newDettPromoDTOs = promoDto.getDettPromos().stream()
                .filter(dettPromoDTO -> finalPromo.getDettPromos().stream()
                        .noneMatch(dettPromo -> dettPromo.getId().equals(dettPromoDTO.getId())))
                .toList();
        newDettPromoDTOs.forEach(dettPromoDTO -> {
            DettPromo dettPromo = new DettPromo();
            finalPromo.getDettPromos().add(dettPromo);
            notFoundTipoPromos.addAll(updateDettPromo(dettPromo, dettPromoDTO, finalPromo));
        });

        if (!notFoundTipoPromos.isEmpty())
            throw new NotFoundException(String.join(",", notFoundTipoPromos));

        promo = promoRepository.save(promo);
        result = createPromoDTO(promo, authHeader);
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @CacheEvict(value = {"promos", "promosActive", "prezziPromosByCodArtAndIsFidAndCodFid"}, allEntries = true)
    @Transactional(rollbackFor = NotFoundException.class)
    public PromoDTO deleteByIdPromo(String idPromo, String authHeader) throws NotFoundException {
        log.debug("deleteByIdPromo(idPromo={})", idPromo);
        log.debug(VERIFICHIAMO_LA_PRESENZA_DELLA_PROMO_PASSATA);
        Promo promo = promoRepository.findById(idPromo)
                .orElseThrow(() -> new NotFoundException(errMessage.getMessage(NOT_FOUND_PROMO_ID_PROMO,
                        new String[]{idPromo}, LocaleContextHolder.getLocale())));
        promoRepository.delete(promo);
        evictSingleCacheValue("promoByIdPromo", idPromo);
        evictSingleCacheValue("promoByCodiceAndAnno", promo.getCodice().concat("-").concat(promo.getAnno().toString()));
        return createPromoDTO(promo, authHeader);
    }

    @Override
    @CacheEvict(value = {"promos", "promosActive", "promoByIdPromo", "promoByCodiceAndAnno", "prezziPromosByCodArtAndIsFidAndCodFid"}, allEntries = true)
    @Transactional
    public List<PromoDTO> deleteAll(String authHeader) {
        log.debug("deleteAll()");
        log.debug(VERIFICHIAMO_LA_PRESENZA_DELLA_PROMO_PASSATA);
        List<Promo> promos = promoRepository.findAll();
        promoRepository.deleteAll();
        return promos.stream().map(promo -> createPromoDTO(promo, authHeader)).toList();
    }

    @Override
    @Cacheable(value = "promoByIdPromo", key = "#idPromo")
    public PromoDTO findById(String idPromo, String authHeader) throws NotFoundException {
        log.debug("findById(idPromo={})", idPromo);
        PromoDTO result = promoRepository.findById(idPromo)
                .map(p -> createPromoDTO(p, authHeader))
                .orElseThrow(() -> new NotFoundException(
                        errMessage.getMessage(NOT_FOUND_PROMO_ID_PROMO, new String[]{idPromo},
                                LocaleContextHolder.getLocale())));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "promoByCodiceAndAnno", key = "#codice.concat('-').concat(#anno.toString())")
    public PromoDTO findByAnnoAndCodice(Integer anno, String codice, String authHeader) throws NotFoundException {
        log.debug("findById(anno={}, codice={})", anno, codice);
        PromoDTO result = promoRepository.findByAnnoAndCodice(anno, codice)
                .map(p -> createPromoDTO(p, authHeader))
                .orElseThrow(() -> new NotFoundException(
                        errMessage.getMessage("NotFound.Promo.annoAndCodice", new String[]{anno.toString(), codice},
                                LocaleContextHolder.getLocale())));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "promosActive", key = "#root.methodName")
    public List<PromoDTO> findByActive(String authHeader) {
        log.debug("findByActive()");
        List<PromoDTO> result = promoRepository.selPromoActive().stream()
                .map(p -> createPromoDTO(p, authHeader))
                .toList();
        log.trace(RETURN_VALUE, result);
        return result;
    }

    private PromoDTO createPromoDTO(Promo p, String authHeader) {
        if (p == null) return null;
        PromoDTO result = modelMapper.map(p, PromoDTO.class);
        result.getDettPromos().forEach(dettPromoDTO -> {
            ArticoliDTO articoliDTO = getArticolo(dettPromoDTO.getCodArt(), authHeader);
            dettPromoDTO.setPrezzo(articoliDTO.getPrezzo());
            dettPromoDTO.setDesArt(articoliDTO.getDescrizione());
        });
        return result;
    }

    @Override
    @Cacheable(value = "promos", key = "#root.methodName")
    public List<PromoDTO> findAllOrderByIdPromo(String authHeader) {
        log.debug("findAllOrderByIdPromo())");
        List<Promo> promos = promoRepository.findAll();
        List<PromoDTO> result = promos.stream().map(dto -> createPromoDTO(dto, authHeader)).toList();
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    protected void evictSingleCacheValue(String cacheName, String cacheKey) {
        log.warn("Cache {} con chiave {} eliminata", cacheName, cacheKey);
        Objects.requireNonNull(cacheManager.getCache(cacheName)).evict(cacheKey);
    }

    protected TipoPromo getTipoPromo(String idTipoPromo) throws NotFoundException {
        return tipoPromoService.findById(Integer.parseInt(idTipoPromo))
                .orElseThrow(() -> new NotFoundException(errMessage.getMessage("NotFound.TipoPromo.idTipoPromo",
                        new String[]{idTipoPromo}, LocaleContextHolder.getLocale())));
    }

    protected List<String> updateDettPromo(DettPromo dettPromo, IDettPromoDTO dettPromoDTO, Promo promo) {
        List<String> notFoundTipoPromos = new ArrayList<>();
        dettPromo.setRiga(dettPromoDTO.getRiga());
        dettPromo.setCodArt(dettPromoDTO.getCodArt().trim());
        dettPromo.setCodFid(dettPromoDTO.getCodFid().trim());
        dettPromo.setInizio(dettPromoDTO.getInizio());
        dettPromo.setFine(dettPromoDTO.getFine());
        try {
            dettPromo.setTipoPromo(getTipoPromo(dettPromoDTO.getIdTipoPromo()));
        } catch (NotFoundException e) {
            notFoundTipoPromos.add(e.getMessage());
        }
        dettPromo.setOggetto(dettPromoDTO.getOggetto().trim());
        dettPromo.setIsFid(dettPromoDTO.getIsFid().trim());
        dettPromo.setPromo(promo);
        return notFoundTipoPromos;
    }

    protected void updateDepRifPromo(DepRifPromo depRifPromo, DepRifPromoDTO depRifPromoDTO, Promo promo) {
        depRifPromo.setIdDeposito(depRifPromoDTO.getIdDeposito());
        depRifPromo.setPromo(promo);
    }

    protected ArticoliDTO getArticolo(String codArt, String authHeader) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(Resilience4JConfiguration.CIRCUIT_BREAKER);

        try {
            ResponseEntity<ArticoliDTO> result = circuitBreaker.run(() -> articoliClient.getArticolo(authHeader, codArt),
                    throwable -> selArticoloFallback(codArt, authHeader));
            return result.getBody();
        } catch (FeignException ex) {
            log.warn("Errore: {}", ex.getLocalizedMessage());
        }

        return ArticoliDTO.builder().descrizione("").prezzo(0.0).build();
    }

    protected ResponseEntity<ArticoliDTO> selArticoloFallback(String codArt, String authHeader) {
        log.warn("****** selDescrizioneFallback in esecuzione ******");
        return articoliClient.getDefArticolo(authHeader, codArt);
    }
}
