package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticoliService {

    List<ArticoliDto> selByDescrizione(String descrizione);

    List<ArticoliDto> selByDescrizione(String descrizione, Pageable pageable);

    ArticoliDto selByCodArt(String codArt);

    Articoli selByCodArt2(String codart);

    ArticoliDto selByBarCode(String barcode);

    void delArticolo(Articoli articolo);

    void insArticolo(Articoli articolo);

    void cleanCaches();
}
