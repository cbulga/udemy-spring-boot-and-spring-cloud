package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "articoli")
public class ArticoliServiceImpl implements ArticoliService {

    @Autowired
    private ArticoliRepository articoliRepository;
    @Autowired
    private ModelMapper modelMapper;

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
        Articoli articoli = articoliRepository.findByCodArt(codArt);
        return articoli != null ? modelMapper.map(articoli, ArticoliDto.class) : null;
    }

    @Override
    public Articoli selByCodArt2(String codart) {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    @Cacheable
    public ArticoliDto selByBarCode(String barCode) {
        Articoli articoli = articoliRepository.selByEan(barCode);
        return articoli != null ? modelMapper.map(articoli, ArticoliDto.class) : null;
    }

    @Override
    @Transactional
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable senza parametri avranno la cache svuotata
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            // tutti i metodi che usano @Cacheable con key = "#codArt" avranno la cache svuotata
            @CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
    })
    public void delArticolo(Articoli articolo) {
        articoliRepository.delete(articolo);
    }

    @Override
    @Transactional
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable senza parametri avranno la cache svuotata
            @CacheEvict(cacheNames = "articoli", allEntries = true),
            // tutti i metodi che usano @Cacheable con key = "#codArt" avranno la cache svuotata
            @CacheEvict(cacheNames = "articolo", key = "#articolo.codArt")
    })
    public void insArticolo(Articoli articolo) {
        articoliRepository.save(articolo);
    }
}
