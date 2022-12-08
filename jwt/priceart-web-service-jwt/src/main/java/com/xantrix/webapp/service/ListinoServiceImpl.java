package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ListiniDTO;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.repository.ListinoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = "listini")
@Slf4j
public class ListinoServiceImpl implements ListinoService {

    private final ListinoRepository listinoRepository;
    private final ModelMapper modelMapper;

    public ListinoServiceImpl(ListinoRepository listinoRepository, ModelMapper modelMapper) {
        this.listinoRepository = listinoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "listini", allEntries = true),
            @CacheEvict(cacheNames = "prezzo", allEntries = true)
    })
    public void insListino(ListiniDTO listiniDto) {
        Listini listini = modelMapper.map(listiniDto, Listini.class);
        listini.getDettListini().forEach(o -> o.setListino(listini));
        listinoRepository.save(listini);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "listini", allEntries = true),
            @CacheEvict(cacheNames = "prezzo", allEntries = true)
    })
    public void delListino(String idList) throws NotFoundException {
        Optional<Listini> listini = selById(idList);

        if (listini.isEmpty()) {
            String msgErr = String.format("Listino %s non presente in anagrafica!", idList);
            log.warn(msgErr);
            throw new NotFoundException(msgErr);
        }

        listinoRepository.delete(listini.get());
    }

    @Override
    public Optional<Listini> selById(String id) {
        return listinoRepository.findById(id);
    }
}
