package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.DettListini;

public interface PrezziService {

    DettListini selPrezzo(String codArt, String listinoId);

    void delPrezzo(String codArt, String listinoId);
}
