package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.Ordini;

import java.util.Optional;

public interface OrdiniService {

    Optional<Ordini> findById(String id);

    void insOrdine(Ordini ordini);

    void delOrdine(Ordini ordini);

    double selValTot(String id);
}
