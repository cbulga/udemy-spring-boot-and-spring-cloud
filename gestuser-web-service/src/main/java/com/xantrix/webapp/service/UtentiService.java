package com.xantrix.webapp.service;

import com.xantrix.webapp.model.Utenti;

import java.util.List;

public interface UtentiService {
    public List<Utenti> selTutti();

    public Utenti selUser(String UserId);

    public void save(Utenti utente);

    public void delete(Utenti utente);
}
