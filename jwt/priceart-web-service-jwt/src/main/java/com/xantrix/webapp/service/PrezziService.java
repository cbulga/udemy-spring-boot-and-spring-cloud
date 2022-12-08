package com.xantrix.webapp.service;

import com.xantrix.webapp.dtos.DettListiniDTO;

public interface PrezziService {

    DettListiniDTO selPrezzo(String codArt, String listinoId);

    void delPrezzo(String codArt, String listinoId);

    void cleanCaches();
}
