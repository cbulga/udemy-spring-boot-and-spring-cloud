package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.DettPromoDTO;
import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.repository.PrezziPromoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PrezziPromoServiceImpl implements PrezziPromoService {

    static final String ACTIVE_DETT_PROMO_NOT_FOUND = "Non è stata trovata nessuna promozione attiva per l'articolo {}. Restituisco {}";
    static final String MORE_THAN_ONE_ACTIVE_DETT_PROMO_FOUND = "E' stata trovata piu' di una promozione attiva per l'articolo {}. Restituisco quella con il prezzo minore";
    static final String OGGETTO_ZERO = "0.0";
    static final String RETURN_VALUE = "return value is {}";
    private final PrezziPromoRepository prezziPromoRepository;
    private final ModelMapper modelMapper;

    public PrezziPromoServiceImpl(PrezziPromoRepository prezziPromoRepository, ModelMapper modelMapper) {
        this.prezziPromoRepository = prezziPromoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-No-null')")
    public DettPromoDTO selByCodArtAndPromoAttiva(String codArt) {
        log.debug("selByCodArtAndPromoAttiva(codArt={})", codArt);
        DettPromoDTO result = calculateDettPromo(codArt, prezziPromoRepository.selByCodArtAndPromoAttiva(codArt));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-Si-null')")
    public DettPromoDTO selByCodArtAndFidAndPromoAttiva(String codArt) {
        log.debug("selByCodArtAndFidAndPromoAttiva(codArt={})", codArt);
        DettPromoDTO result = calculateDettPromo(codArt, prezziPromoRepository.selByCodArtAndFidAndPromoAttiva(codArt));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-Si-').concat(#codFid)")
    public DettPromoDTO selByCodArtAndCodFidAndPromoAttiva(String codArt, String codFid) {
        log.debug("selByCodArtAndCodFidAndPromoAttiva(codArt={}, codFid={})", codArt, codFid);
        DettPromoDTO result = calculateDettPromo(codArt, prezziPromoRepository.selByCodArtAndCodFidAndPromoAttiva(codArt, codFid));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    DettPromoDTO calculateDettPromo(String codArt, List<DettPromo> dettPromos) {
        DettPromo dettPromo;
        if (dettPromos == null || dettPromos.isEmpty()) {
            log.warn(ACTIVE_DETT_PROMO_NOT_FOUND, codArt, OGGETTO_ZERO);
            dettPromo = DettPromo.builder().oggetto(OGGETTO_ZERO).build();
        } else if (dettPromos.size() > 1) {
            log.warn(MORE_THAN_ONE_ACTIVE_DETT_PROMO_FOUND, codArt);
            dettPromo = dettPromos.stream()
                    .min(Comparator.comparingDouble(o -> Double.parseDouble(o.getOggetto())))
                    .orElse(null);
        } else
            dettPromo = dettPromos.get(0);
        return modelMapper.map(dettPromo, DettPromoDTO.class);
    }
}
