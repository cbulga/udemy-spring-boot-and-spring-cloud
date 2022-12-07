package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.TipoPromo;
import com.xantrix.webapp.repository.TipoPromoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Slf4j
@CacheConfig(cacheNames = "tipoPromo")
@Service
public class TipoPromoServiceImpl implements TipoPromoService {

    static final String RETURN_VALUE = "return value is {}";
    private final TipoPromoRepository tipoPromoRepository;

    public TipoPromoServiceImpl(TipoPromoRepository tipoPromoRepository) {
        this.tipoPromoRepository = tipoPromoRepository;
    }

    @Cacheable(value = "tipoPromo")
    public Optional<TipoPromo> findById(Integer idTipoPromo) {
        log.debug("findById(idTipoPromo){})", idTipoPromo);
        Optional<TipoPromo> result = tipoPromoRepository.findById(idTipoPromo);
        log.trace(RETURN_VALUE, result);
        return result;
    }
}
