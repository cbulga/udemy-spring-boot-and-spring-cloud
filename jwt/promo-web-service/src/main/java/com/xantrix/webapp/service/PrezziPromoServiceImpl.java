package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.DettPromo;
import com.xantrix.webapp.repository.PrezziPromoRepository;
import lombok.extern.slf4j.Slf4j;
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

    public PrezziPromoServiceImpl(PrezziPromoRepository prezziPromoRepository) {
        this.prezziPromoRepository = prezziPromoRepository;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-No-null')")
    public Double selByCodArtAndPromoAttiva(String codArt) {
        log.debug("selByCodArtAndPromoAttiva(codArt={})", codArt);
        Double result = calculateOggetto(codArt, prezziPromoRepository.selByCodArtAndPromoAttiva(codArt));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-Si-null')")
    public Double selByCodArtAndFidAndPromoAttiva(String codArt) {
        log.debug("selByCodArtAndFidAndPromoAttiva(codArt={})", codArt);
        Double result = calculateOggetto(codArt, prezziPromoRepository.selByCodArtAndFidAndPromoAttiva(codArt));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Cacheable(value = "prezziPromosByCodArtAndIsFidAndCodFid", key = "#codArt.concat('-Si-').concat(#codFid)")
    public Double selByCodArtAndCodFidAndPromoAttiva(String codArt, String codFid) {
        log.debug("selByCodArtAndCodFidAndPromoAttiva(codArt={}, codFid={})", codArt, codFid);
        Double result = calculateOggetto(codArt, prezziPromoRepository.selByCodArtAndCodFidAndPromoAttiva(codArt, codFid));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    Double calculateOggetto(String codArt, List<DettPromo> dettPromos) {
        Double result;
        if (dettPromos == null || dettPromos.isEmpty()) {
            log.warn(ACTIVE_DETT_PROMO_NOT_FOUND, codArt, OGGETTO_ZERO);
            result = 0.0D;
        } else if (dettPromos.size() > 1) {
            log.warn(MORE_THAN_ONE_ACTIVE_DETT_PROMO_FOUND, codArt);
            result = dettPromos.stream()
                    .min(Comparator.comparingDouble(o -> Double.parseDouble(o.getOggetto())))
                    .map(o -> Double.parseDouble(o.getOggetto().replace(",", ".")))
                    .orElse(null);
        } else
            result = Double.parseDouble(dettPromos.get(0).getOggetto().replace(",", "."));
        return result;
    }
}
