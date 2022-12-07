package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entity.Articoli;
import com.xantrix.webapp.entity.Barcode;
import com.xantrix.webapp.repository.ArticoliRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ArticoliRepository articoliRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CacheManager cacheManager;

    @Override
    @Cacheable
    public List<ArticoliDto> selByDescrizione(String descrizione) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%");
        return !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDto.class))
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    @Cacheable
    public List<ArticoliDto> selByDescrizione(String descrizione, Pageable pageable) {
        List<Articoli> articolis = articoliRepository.findByDescrizioneLike("%" + descrizione.toUpperCase() + "%", pageable);
        return !CollectionUtils.isEmpty(articolis) ? articolis.stream()
                .filter(Objects::nonNull)
                .map(a -> modelMapper.map(a, ArticoliDto.class))
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    @Cacheable(value = "articolo", key = "#codArt", sync = true)
    public ArticoliDto selByCodArt(String codArt) {
        Articoli articoli = selByCodArt2(codArt);
        return articoli != null ? modelMapper.map(articoli, ArticoliDto.class) : null;
    }

    @Override
    public Articoli selByCodArt2(String codart) {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    @Cacheable(value = "barcode", key = "#barcode", sync = true)
    public ArticoliDto selByBarCode(String barcode) {
        Articoli articoli = articoliRepository.selByEan(barcode);
        return articoli != null ? modelMapper.map(articoli, ArticoliDto.class) : null;
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

            cacheManager.getCache("barcode").evict(barcode.getBarcode());
        });
    }

    @Override
    public void cleanCaches() {
        cacheManager.getCacheNames().forEach(item -> {
            log.info(String.format("Eliminazione cache %s", item));
            cacheManager.getCache(item).clear();
        });
    }
}
