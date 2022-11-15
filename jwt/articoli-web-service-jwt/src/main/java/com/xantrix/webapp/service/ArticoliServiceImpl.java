package com.xantrix.webapp.service;

import com.xantrix.webapp.Resilience4JConfiguration;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.PrezzoDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.feign.PriceClient;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = {"articoli"})
@Slf4j
public class ArticoliServiceImpl implements ArticoliService {

    private final ArticoliRepository articoliRepository;
    private final ModelMapper modelMapper;
    private final CacheManager cacheManager;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final PriceClient priceClient;

    public ArticoliServiceImpl(ArticoliRepository articoliRepository, ModelMapper modelMapper, CacheManager cacheManager, CircuitBreakerFactory<?, ?> circuitBreakerFactory, PriceClient priceClient) {
        this.articoliRepository = articoliRepository;
        this.modelMapper = modelMapper;
        this.cacheManager = cacheManager;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.priceClient = priceClient;
    }

    @Override
    @Cacheable
    public List<ArticoliDto> selByDescrizione(String descrizione, String idList, String authHeader) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%");
        List<ArticoliDto> articoliDto = !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDto.class))
                .collect(Collectors.toList()) : Collections.emptyList();
        articoliDto.forEach(a -> a.setPrezzo(getPriceArt(a.getCodArt(), idList, authHeader)));
        return articoliDto;
    }

    @Override
    @Cacheable
    public List<ArticoliDto> selByDescrizione(String descrizione, String idList, String authHeader, Pageable pageable) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%", pageable);
        List<ArticoliDto> articoliDto = !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDto.class))
                .collect(Collectors.toList()) : Collections.emptyList();
        articoliDto.forEach(a -> a.setPrezzo(getPriceArt(a.getCodArt(), idList, authHeader)));
        return articoliDto;
    }

    @Override
    @Cacheable(value = "articolo", key = "#codArt", sync = true)
    public ArticoliDto selByCodArt(String codArt, String idList, String authHeader) {
        Articoli articoli = selByCodArt2(codArt);
        ArticoliDto articoliDto;
        if (articoli != null) {
            articoliDto = modelMapper.map(articoli, ArticoliDto.class);
            articoliDto.setPrezzo(getPriceArt(codArt, idList, authHeader));
        } else articoliDto = null;
        return articoliDto;
    }

    @Override
    public Articoli selByCodArt2(String codart) {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    @Cacheable(value = "barcode", key = "#barcode", sync = true)
    public ArticoliDto selByBarCode(String barcode, String idList, String authHeader) {
        Articoli articoli = articoliRepository.selByEan(barcode);
        ArticoliDto articoliDto = articoli != null ? modelMapper.map(articoli, ArticoliDto.class) : null;
        if (articoliDto != null)
            articoliDto.setPrezzo(getPriceArt(articoliDto.getCodArt(), idList, authHeader));
        return articoliDto;
    }

    @Override
    @Transactional
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable senza parametri avranno la cache svuotata
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            // tutti i metodi che usano @Cacheable con key = "#barcode" avranno la cache svuotata
//            @CacheEvict(cacheNames = "barcode", key = "#articolo.barcode[0].barcode"),
            // tutti i metodi che usano @Cacheable con key = "#codArt" avranno la cache svuotata
            @CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
    })
    public void delArticolo(Articoli articolo) {
        articoliRepository.delete(articolo);
        this.evictCache(articolo.getBarcode());
    }

    @Override
    @Transactional
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable senza parametri avranno la cache svuotata
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            // tutti i metodi che usano @Cacheable con key = "#barcode" avranno la cache svuotata per quel barcode
//            @CacheEvict(cacheNames = "barcode", key = "#articolo.barcode[0].barcode"),
            // tutti i metodi che usano @Cacheable con key = "#codArt" avranno la cache svuotata per quell'articolo
            @CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
    })
    public void insArticolo(Articoli articolo) {
        articoliRepository.save(articolo);
        this.evictCache(articolo.getBarcode());
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

    protected double getPriceArt(String codArt, String idList, String authHeader) {
        double prezzo = 0.0;

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(Resilience4JConfiguration.CIRCUIT_BREAKER);

        try {
            ResponseEntity<PrezzoDto> result = StringUtils.isNotEmpty(idList)
                    ? circuitBreaker.run(() -> priceClient.getPriceArt2(authHeader, codArt, idList), throwable -> selPrezzoFallback(codArt, authHeader))
                    : circuitBreaker.run(() -> priceClient.getDefPriceArt2(authHeader, codArt), throwable -> selPrezzoFallback(codArt, authHeader));
            PrezzoDto prezzoDto = result.getBody();
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
            log.warn("Errore: {}", ex.getLocalizedMessage());
        }

        return prezzo;
    }

    protected ResponseEntity<PrezzoDto> selPrezzoFallback(String codArt, String authHeader) {
        log.warn("****** selPrezzoFallback in esecuzione ******");
        return priceClient.getDefPriceArt2(authHeader, codArt);
    }
}
