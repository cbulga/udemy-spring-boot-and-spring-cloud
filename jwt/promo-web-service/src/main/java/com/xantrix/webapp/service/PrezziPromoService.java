package com.xantrix.webapp.service;

public interface PrezziPromoService {

    Double selByCodArtAndPromoAttiva(String codArt);

    Double selByCodArtAndFidAndPromoAttiva(String codArt);

    Double selByCodArtAndCodFidAndPromoAttiva(String codArt, String codFid);
}
