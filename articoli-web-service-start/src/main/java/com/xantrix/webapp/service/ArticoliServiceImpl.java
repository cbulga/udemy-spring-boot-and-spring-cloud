package com.xantrix.webapp.service;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArticoliServiceImpl implements ArticoliService {

    @Autowired
    private ArticoliRepository articoliRepository;

    @Override
    public List<Articoli> selByDescrizione(String descrizione) {
        return articoliRepository.findByDescrizioneLike(descrizione);
    }

    @Override
    public Articoli selByCodArt(String codArt) {
        return articoliRepository.findByCodArt(codArt);
    }

    @Override
    public Articoli selByBarCode(String barCode) {
        return articoliRepository.selByEan(barCode);
    }

    @Override
    @Transactional
    public void delArticolo(Articoli articolo) {
        articoliRepository.delete(articolo);
    }

    @Override
    @Transactional
    public void insArticolo(Articoli articolo) {
        articoliRepository.save(articolo);
    }
}
