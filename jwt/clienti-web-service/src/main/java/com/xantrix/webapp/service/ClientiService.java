package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.ClientiDTO;
import com.xantrix.webapp.exception.NotFoundException;

import java.util.List;

public interface ClientiService {

    ClientiDTO findByCodice(String codice) throws NotFoundException;

    void cleanCaches();

    void updateBolliniByCodice(Integer bollini, String codice) throws NotFoundException;

    List<ClientiDTO> findByCognome(String cognome);
}
