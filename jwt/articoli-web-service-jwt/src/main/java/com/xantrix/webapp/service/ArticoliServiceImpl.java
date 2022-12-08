package com.xantrix.webapp.service;

import com.xantrix.webapp.config.Resilience4JConfiguration;
import com.xantrix.webapp.dtos.ArticoliDTO;
import com.xantrix.webapp.dtos.PrezzoDTO;
import com.xantrix.webapp.entity.Articoli;
import com.xantrix.webapp.entity.Barcode;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.feign.PriceClient;
import com.xantrix.webapp.feign.PromoClient;
import com.xantrix.webapp.repository.ArticoliRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = {"articoli"})
@Slf4j
public class ArticoliServiceImpl implements ArticoliService {

    public static final String ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA_IMPOSSIBILE_UTILIZZARE_IL_METODO_PUT = "Articolo %s non presente in anagrafica! Impossibile utilizzare il metodo PUT";
    public static final String ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST = "Articolo %s presente in anagrafica! Impossibile utilizzare il metodo POST";
    public static final String ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA = "Articolo %s non presente in anagrafica!";
    private final ArticoliRepository articoliRepository;
    private final ModelMapper modelMapper;
    private final CacheManager cacheManager;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final PriceClient priceClient;
    private final PromoClient promoClient;

//    //HISTRIX CONSTANT SETTINGS
//    // altre opzioni https://github.com/Netflix/Hystrix/wiki/Configuration
//    public static final String FAILURE_TIMOUT_IN_MS = "6000"; //Timeout in ms prima di failure e fallback logic (def 1000)
//    public static final String REQUEST_VOLUME_THRESHOLD = "10"; //Numero Minimo di richieste prima di aprire il circuito (Def 20)
//    public static final String ERROR_THRESHOLD_PERCENTAGE = "30"; //Percentuale minima di fallimenti prima di apertura del circuito (Def 50)
//    public static final String SLEEP_TIME_IN_MS = "5000"; //Tempo in ms prima di ripetere tentativo di chiusura del circuito (def 5000) (cioè quanto tempo il circuito resta aperto prima di essere richiuso)
//    public static final String TIME_METRIC_IN_MS = "5000"; //Tempo base in ms delle metriche statistiche (se in 5 secondi ho almeno il 30% (ERROR_THRESHOLD_PERCENTAGE) di richieste in fallimento o 10 (REQUEST_VOLUME_THRESHOLD) richieste fallite, allora il circuito viene aperto)

    public ArticoliServiceImpl(ArticoliRepository articoliRepository, ModelMapper modelMapper, CacheManager cacheManager, CircuitBreakerFactory<?, ?> circuitBreakerFactory, PriceClient priceClient, PromoClient promoClient) {
        this.articoliRepository = articoliRepository;
        this.modelMapper = modelMapper;
        this.cacheManager = cacheManager;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.priceClient = priceClient;
        this.promoClient = promoClient;
    }

    @Override
    @Cacheable(value = "articoli", key = "#descrizione.concat('-').concat(#idList)")
//    @HystrixCommand(fallbackMethod = "selByDescrizioneFallBack",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = FAILURE_TIMOUT_IN_MS),
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = REQUEST_VOLUME_THRESHOLD),
//                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = ERROR_THRESHOLD_PERCENTAGE),
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = SLEEP_TIME_IN_MS),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = TIME_METRIC_IN_MS)
//            })
    public List<ArticoliDTO> selByDescrizione(String descrizione, String idList, String authHeader) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%");
        List<ArticoliDTO> articoliDto = !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDTO.class))
                .collect(Collectors.toList()) : Collections.emptyList();
        articoliDto.forEach(a -> {
            a.setPrezzo(getPriceArt(a.getCodArt(), idList, authHeader));
            a.setPrezzoPromo(getPricePromo(a.getCodArt(), authHeader));
        });
        return articoliDto;
    }

//    @SuppressWarnings("unused")
//    public List<ArticoliDto> selByDescrizioneFallBack(String descrizione, String idList, String authHeader) {
//        log.warn("****** selByDescrizioneFallBack in esecuzione *******");
//        //TODO: prevedere la lettura da fonte dati alternativa
//        return Collections.emptyList();
//    }

    @Override
    @Cacheable(value = "articoli", key = "#descrizione.concat('-').concat(#idList).concat('-').concat(#pageable)")
//    @HystrixCommand(fallbackMethod = "selByDescrizioneFallBack",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = FAILURE_TIMOUT_IN_MS),
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = REQUEST_VOLUME_THRESHOLD),
//                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = ERROR_THRESHOLD_PERCENTAGE),
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = SLEEP_TIME_IN_MS),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = TIME_METRIC_IN_MS)
//            })
    public List<ArticoliDTO> selByDescrizione(String descrizione, String idList, String authHeader, Pageable pageable) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%", pageable);
        List<ArticoliDTO> articoliDto = !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDTO.class))
                .collect(Collectors.toList()) : Collections.emptyList();
        articoliDto.forEach(a -> {
            a.setPrezzo(getPriceArt(a.getCodArt(), idList, authHeader));
            a.setPrezzoPromo(getPricePromo(a.getCodArt(), authHeader));
        });
        return articoliDto;
    }

//    @SuppressWarnings("unused")
//    public List<ArticoliDto> selByDescrizioneFallBack(String descrizione, String idList, String authHeader, Pageable pageable) {
//        log.warn("****** selByDescrizioneFallBack in esecuzione *******");
//        //TODO: prevedere la lettura da fonte dati alternativa
//        return Collections.emptyList();
//    }

    @Override
    @Cacheable(value = "articolo", key = "#codArt.concat('-').concat(#idList)", unless="#result == null")
//    @HystrixCommand(fallbackMethod = "selByCodArtFallBack",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = FAILURE_TIMOUT_IN_MS),
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = REQUEST_VOLUME_THRESHOLD),
//                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = ERROR_THRESHOLD_PERCENTAGE),
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = SLEEP_TIME_IN_MS),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = TIME_METRIC_IN_MS)
//            })
    public ArticoliDTO selByCodArt(String codArt, String idList, String authHeader) {
        Articoli articoli = selByCodArt2(codArt);
        ArticoliDTO articoliDto;
        if (articoli != null) {
            articoliDto = modelMapper.map(articoli, ArticoliDTO.class);
            articoliDto.setPrezzo(getPriceArt(codArt, idList, authHeader));
            articoliDto.setPrezzoPromo(getPricePromo(codArt, authHeader));
        } else articoliDto = null;
        return articoliDto;
    }

//    @SuppressWarnings("unused")
//    public ArticoliDto setByCodArtFallBack(String codArt, String idList, String authHeader) {
//        log.warn("****** setByCodArtFallBack in esecuzione *******");
//        //TODO: prevedere la lettura da fonte dati alternativa
//        return null;
//    }

    @Override
    public Articoli selByCodArt2(String codart) {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    @Cacheable(value = "barcode", key = "#barcode.concat('-').concat(#idList)", unless="#result == null")
//    @HystrixCommand(fallbackMethod = "selByBarCodeFallBack",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = FAILURE_TIMOUT_IN_MS),
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = REQUEST_VOLUME_THRESHOLD),
//                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = ERROR_THRESHOLD_PERCENTAGE),
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = SLEEP_TIME_IN_MS),
//                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = TIME_METRIC_IN_MS)
//            })
    public ArticoliDTO selByBarCode(String barcode, String idList, String authHeader) {
        Articoli articoli = articoliRepository.selByEan(barcode);
        ArticoliDTO articoliDto = articoli != null ? modelMapper.map(articoli, ArticoliDTO.class) : null;
        if (articoliDto != null) {
            articoliDto.setPrezzo(getPriceArt(articoliDto.getCodArt(), idList, authHeader));
            articoliDto.setPrezzoPromo(getPricePromo(articoliDto.getCodArt(), authHeader));
        }
        return articoliDto;
    }
//
//    @SuppressWarnings("unused")
//    public ArticoliDto selByBarCodeFallBack(String barcode, String idList, String authHeader) {
//        log.warn("****** selByBarCodeFallBack in esecuzione *******");
//        //TODO: prevedere la lettura da fonte dati alternativa
//        return null;
//    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            @CacheEvict(cacheNames = "articolo", allEntries = true),
            @CacheEvict(cacheNames = "barcode", allEntries = true)
    })
    public void delArticolo(String codArt) throws NotFoundException {
        Articoli articoliToDelete = selByCodArt2(codArt);
        if (articoliToDelete == null) {
            String errorMessage = String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA, codArt);
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        articoliRepository.delete(articoliToDelete);
//        this.evictCache(articolo.getBarcode());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            @CacheEvict(cacheNames = "articolo", allEntries = true),
            @CacheEvict(cacheNames = "barcode", allEntries = true)
    })
    public void insArticolo(ArticoliDTO articoliDto) throws DuplicateException {
        log.debug("Verifichiamo la presenza dell'articolo passato");
        Articoli duplicatedArticoli = selByCodArt2(articoliDto.getCodArt());
        if (duplicatedArticoli != null) {
            String errorMessage = String.format(ARTICOLO_DUPLICATO_IMPOSSIBILE_UTILIZZARE_IL_METODO_POST, articoliDto.getCodArt());
            log.warn(errorMessage);
            throw new DuplicateException(errorMessage);
        }

        Articoli articoli = modelMapper.map(articoliDto, Articoli.class);
        articoli.getBarcode().forEach(barcode -> barcode.setArticolo(articoli));
        articoliRepository.save(articoli);
        this.evictCache(articoli.getBarcode());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            @CacheEvict(cacheNames = "articolo", allEntries = true),
            @CacheEvict(cacheNames = "barcode", allEntries = true)
    })
    public void updArticolo(ArticoliDTO articoliDto) throws NotFoundException {
        log.debug("Verifichiamo la presenza dell'articolo passato");
        Articoli articoliToUpdate = selByCodArt2(articoliDto.getCodArt());
        if (articoliToUpdate == null) {
            String errorMessage = String.format(ARTICOLO_NON_PRESENTE_IN_ANAGRAFICA_IMPOSSIBILE_UTILIZZARE_IL_METODO_PUT, articoliDto.getCodArt());
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        Articoli articoli = modelMapper.map(articoliDto, Articoli.class);
        articoli.getBarcode().forEach(barcode -> barcode.setArticolo(articoliToUpdate));
        this.evictCache(articoliToUpdate.getBarcode());
        articoliToUpdate.getBarcode().clear();
        articoli.getBarcode().forEach(barcode -> {
            barcode.setArticolo(articoliToUpdate);
            articoliToUpdate.getBarcode().add(barcode);
        });
//        articoliToUpdate.setBarcode(articoli.getBarcode());
        articoliToUpdate.setDescrizione(articoli.getDescrizione());
        articoliToUpdate.setUm(articoli.getUm());
        articoliToUpdate.setCodStat(articoli.getCodStat());
        articoliToUpdate.setIva(articoli.getIva());
        articoliToUpdate.setDataCreaz(articoli.getDataCreaz());
        articoliToUpdate.setIngredienti(articoli.getIngredienti());
        articoliToUpdate.setFamAssort(articoli.getFamAssort());
        articoliToUpdate.setIdStatoArt(articoli.getIdStatoArt());
        articoliToUpdate.setPesoNetto(articoli.getPesoNetto());
        articoliToUpdate.setPzCart(articoli.getPzCart());
        articoliRepository.save(articoliToUpdate);
    }

    private void evictCache(Set<Barcode> ean) {
        ean.forEach((Barcode barcode) -> {
            log.info("Eliminazione cache barcode " + barcode.getBarcode());

            Objects.requireNonNull(cacheManager.getCache("barcode")).evict(barcode.getBarcode());
        });
    }

    @Override
    public void cleanCaches() {
        cacheManager.getCacheNames().forEach(item -> {
            log.info(String.format("Eliminazione cache %s", item));
            Objects.requireNonNull(cacheManager.getCache(item)).clear();
        });
    }

    protected double getPricePromo(String codArt, String authHeader) {
        double prezzoPromo = 0.0;

        log.info("Ottenimento Prezzo Promozionale Codice {}", codArt);

        try {
            double prezzo = promoClient.getPromoPrice(authHeader, codArt);
            if (prezzo > 0)
                log.info("Prezzo promozionale {}", prezzo);
            prezzoPromo = prezzo;
        } catch (FeignException ex) {
            log.warn("Errore: {}", ex.getLocalizedMessage());
        }

        return prezzoPromo;
    }

    protected double getPriceArt(String codArt, String idList, String authHeader) {
        double prezzo = 0.0;

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(Resilience4JConfiguration.CIRCUIT_BREAKER);

        try {
            ResponseEntity<PrezzoDTO> result = StringUtils.isNotEmpty(idList)
                    ? circuitBreaker.run(() -> priceClient.getPriceArt2(authHeader, codArt, idList), throwable -> selPrezzoFallback(codArt, authHeader))
                    : circuitBreaker.run(() -> priceClient.getDefPriceArt2(authHeader, codArt), throwable -> selPrezzoFallback(codArt, authHeader));
            PrezzoDTO prezzoDto = result.getBody();
            assert prezzoDto != null;
            log.info("Prezzo articolo {}: {}", prezzoDto.getCodArt(), prezzoDto.getPrezzo());
            if (prezzoDto.getSconto() > 0) {
                log.info("Attivato sconto: {}%", prezzoDto.getSconto());
                prezzo = prezzoDto.getPrezzo() * (1 - (prezzoDto.getSconto() / 100));
                prezzo *= 100;
                prezzo = Math.round(prezzo);
                prezzo /= 100;
            } else
                prezzo = prezzoDto.getPrezzo();
        } catch (FeignException ex) {
            log.warn(ex.getMessage(), ex);
        }

        return prezzo;
    }

    protected ResponseEntity<PrezzoDTO> selPrezzoFallback(String codArt, String authHeader) {
        log.warn("****** selPrezzoFallback in esecuzione ******");
        return priceClient.getDefPriceArt2(authHeader, codArt);
    }
}
