package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.repository.PrezziRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PrezziServiceImpl implements PrezziService {

    final PrezziRepository prezziRepository;

    public PrezziServiceImpl(PrezziRepository prezziRepository) {
        this.prezziRepository = prezziRepository;
    }

    @Override
    @Cacheable(value = "prezzo", key = "#codArt.concat('-').concat(#listinoId)", sync = true)
    public DettListini selPrezzo(String codArt, String listinoId) {
        return prezziRepository.selByCodArtAndListinoId(codArt, listinoId);
    }

    @Override
    @Caching(evict = {
            // tutti i metodi che usano @Cacheable con key = "#id" avranno la cache svuotata
            @CacheEvict(cacheNames = "prezzo", key = "#codArt.concat('-').concat(#listinoId)")
    })
    public void delPrezzo(String codArt, String listinoId) {
        prezziRepository.delRowDettList(codArt, listinoId);
    }
}
