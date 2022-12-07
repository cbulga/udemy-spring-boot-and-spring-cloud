package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.TipoPromo;

import java.util.Optional;

public interface TipoPromoService {

    Optional<TipoPromo> findById(Integer idTipoPromo);
}
