package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.TipoPromo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TipoPromoRepository extends JpaRepository<TipoPromo, Integer>, JpaSpecificationExecutor<TipoPromo> {

    @Override
    @Cacheable(value = "tipoPromo")
    Optional<TipoPromo> findById(Integer idTipoPromo);
}