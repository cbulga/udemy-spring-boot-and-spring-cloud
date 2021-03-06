package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.Listini;

import java.util.Optional;

public interface ListinoService {

    Optional<Listini> selById(String id);

    void insListino(Listini listino);

    void delListino(Listini listino);
}
