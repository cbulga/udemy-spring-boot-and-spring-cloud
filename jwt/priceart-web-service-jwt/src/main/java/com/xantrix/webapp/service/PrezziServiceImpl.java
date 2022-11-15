package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.repository.PrezziRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@Slf4j
public class PrezziServiceImpl implements PrezziService {

    final PrezziRepository prezziRepository;

    public PrezziServiceImpl(PrezziRepository prezziRepository) {
        this.prezziRepository = prezziRepository;
    }

    @Override
    @Cacheable(value = "prezzo", key = "#codArt.concat('-').concat(#listinoId)", sync = true)
    public DettListini selPrezzo(String codArt, String listinoId) {
        // questo serve per fare in modo che intervenga il circuit breaker se questo metodo impiega piu' di 3 secondo
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 5000);
        log.warn("Applicato rallentamento di {}", randomNum);
        try {
            Thread.sleep(randomNum);
        } catch (InterruptedException e) {
            log.error("Errore durante la lettura del prezzo", e);
            return null;
        }
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
