package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.ListiniDTO;
import com.xantrix.webapp.entity.Listini;
import com.xantrix.webapp.exception.NotFoundException;

import java.util.Optional;

public interface ListinoService {

    Optional<Listini> selById(String id);

    void insListino(ListiniDTO listino);

    void delListino(String idList) throws NotFoundException;
}
