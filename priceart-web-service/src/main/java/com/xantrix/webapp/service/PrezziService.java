package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.DettListini;

public interface PrezziService {

    DettListini selPrezzo(String CodArt, String Listino);

    void delPrezzo(String CodArt, String IdList);
}
