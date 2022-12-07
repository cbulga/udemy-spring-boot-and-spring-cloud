package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.DettPromoDTO;

public interface PrezziPromoService {

    DettPromoDTO selByCodArtAndPromoAttiva(String codArt);

    DettPromoDTO selByCodArtAndFidAndPromoAttiva(String codArt);

    DettPromoDTO selByCodArtAndCodFidAndPromoAttiva(String codArt, String codFid);
}
