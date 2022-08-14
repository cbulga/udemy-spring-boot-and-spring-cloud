package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.repository.ListinoRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = "listini")
public class ListinoServiceImpl implements ListinoService {

    final ListinoRepository listinoRepository;

    public ListinoServiceImpl(ListinoRepository listinoRepository) {
        this.listinoRepository = listinoRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "listini", allEntries = true),
            @CacheEvict(cacheNames = "prezzo", allEntries = true)
    })
    public void insListino(Listini listino) {
        listinoRepository.save(listino);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "listini", allEntries = true),
            @CacheEvict(cacheNames = "prezzo", allEntries = true)
    })
    public void delListino(Listini listino) {
        listinoRepository.delete(listino);
    }

    @Override
    public Optional<Listini> selById(String id) {
        return listinoRepository.findById(id);
    }
}
