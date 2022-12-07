package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entity.Articoli;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticoliService {

    List<ArticoliDto> selByDescrizione(String descrizione, String idList, String authHeader);

    List<ArticoliDto> selByDescrizione(String descrizione, String idList, String authHeader, Pageable pageable);

    ArticoliDto selByCodArt(String codArt, String idList, String authHeader);

    Articoli selByCodArt2(String codart);

    ArticoliDto selByBarCode(String barcode, String idList, String authHeader);

    void delArticolo(Articoli articolo);

    void insArticolo(Articoli articolo);

    void cleanCaches();
}
