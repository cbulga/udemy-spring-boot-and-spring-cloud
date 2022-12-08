package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ArticoliDTO;
import com.xantrix.webapp.entity.Articoli;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticoliService {

    List<ArticoliDTO> selByDescrizione(String descrizione, String idList, String authHeader);

    List<ArticoliDTO> selByDescrizione(String descrizione, String idList, String authHeader, Pageable pageable);

    ArticoliDTO selByCodArt(String codArt, String idList, String authHeader);

    Articoli selByCodArt2(String codart);

    ArticoliDTO selByBarCode(String barcode, String idList, String authHeader);

    void delArticolo(String codArt) throws NotFoundException;

    void insArticolo(ArticoliDTO articoliDto) throws DuplicateException;
    void updArticolo(ArticoliDTO articoliDto) throws NotFoundException;

    void cleanCaches();
}
