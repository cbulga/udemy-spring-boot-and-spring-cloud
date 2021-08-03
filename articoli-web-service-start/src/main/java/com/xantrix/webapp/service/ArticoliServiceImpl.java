package com.xantrix.webapp.service;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "articoli")
public class ArticoliServiceImpl implements ArticoliService {

    @Autowired
    private ArticoliRepository articoliRepository;

    @Override
    @Cacheable
    public List<Articoli> selByDescrizione(String descrizione) {
        return articoliRepository.findByDescrizioneLike(descrizione.toUpperCase());
    }

    @Override
    @Cacheable(value = "articolo", key = "#codArt", sync = true)
    public Articoli selByCodArt(String codArt) {
        return articoliRepository.findByCodArt(codArt);
    }

    @Override
    @Cacheable
    public Articoli selByBarCode(String barCode) {
        return articoliRepository.selByEan(barCode);
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
