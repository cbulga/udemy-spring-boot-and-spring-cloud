package com.xantrix.webapp.service;

import com.xantrix.webapp.entities.Articoli;

import java.util.List;

public interface ArticoliService {

    List<Articoli> selByDescrizione(String descrizione);

    Articoli selByCodArt(String codArt);

    Articoli selByBarCode(String barCode);

    void delArticolo(Articoli articolo);

    void insArticolo(Articoli articolo);
}
