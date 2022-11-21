package com.xantrix.webapp.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.xantrix.webapp.entity.DettListini;
import com.xantrix.webapp.repository.PrezziRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
public class PrezziServiceImpl implements PrezziService {

    final PrezziRepository prezziRepository;

    private final CacheManager cacheManager;

    //HISTRIX CONSTANT SETTINGS
    // altre opzioni https://github.com/Netflix/Hystrix/wiki/Configuration
    public static final String FAILURE_TIMOUT_IN_MS = "6000"; //Timeout in ms prima di failure e fallback logic (def 1000)
    public static final String REQUEST_VOLUME_THRESHOLD = "10"; //Numero Minimo di richieste prima di aprire il circuito (Def 20)
    public static final String ERROR_THRESHOLD_PERCENTAGE = "30"; //Percentuale minima di fallimenti prima di apertura del circuito (Def 50)
    public static final String SLEEP_TIME_IN_MS = "5000"; //Tempo in ms prima di ripetere tentativo di chiusura del circuito (def 5000)
    public static final String TIME_METRIC_IN_MS = "5000"; //Tempo base in ms delle metriche statistiche

    public PrezziServiceImpl(PrezziRepository prezziRepository, CacheManager cacheManager) {
        this.prezziRepository = prezziRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "prezzo", key = "#codArt.concat('-').concat(#listinoId)", sync = true)
    @HystrixCommand(fallbackMethod = "selPrezzoFallBack",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = FAILURE_TIMOUT_IN_MS),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = REQUEST_VOLUME_THRESHOLD),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = ERROR_THRESHOLD_PERCENTAGE),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = SLEEP_TIME_IN_MS),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = TIME_METRIC_IN_MS)
            })
    public DettListini selPrezzo(String codArt, String listinoId) {
        // questo serve per fare in modo che intervenga il circuit breaker se questo metodo impiega piu' di 6 secondi
        /*
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 8000);
        log.warn("Applicato rallentamento di {}", randomNum);
        try {
            Thread.sleep(randomNum);
        } catch (InterruptedException e) {
            log.error("Errore durante la lettura del prezzo", e);
            return null;
        }
        */
        return prezziRepository.selByCodArtAndListinoId(codArt, listinoId);
    }

    @SuppressWarnings("unused")
    public DettListini selPrezzoFallBack(String codArt, String listinoId) {
        log.warn("****** SelPrezzoFallBack in esecuzione *******");

        DettListini dettListini = prezziRepository.selByCodArtAndListinoId(codArt, listinoId);

        if (dettListini != null) {
            log.warn("Ottenuto listino dalla fonte dati alternativa");
        } else {
            log.warn("Fonte dati alternativa non disponibile! Impossibile ottenere il listino");
            dettListini = new DettListini();
            dettListini.setCodArt(codArt);
            dettListini.setPrezzo(0.00);
        }

//        this.evictSingleCacheValue("prezzo", codArt.concat("-").concat(listinoId));
        return dettListini;
    }

    public void evictSingleCacheValue(String cacheName, String cacheKey) {
        log.warn("Cache {} con chiave {} eliminata", cacheName, cacheKey);
        cacheManager.getCache(cacheName).evict(cacheKey);
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
