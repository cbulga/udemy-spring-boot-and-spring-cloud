package com.xantrix.webapp.service;

import com.xantrix.webapp.model.Utenti;

import java.util.List;

public interface UtentiService {
    List<Utenti> selTutti();

    Utenti selUser(String userId);

    void save(Utenti utente);

    void delete(Utenti utente);
}
