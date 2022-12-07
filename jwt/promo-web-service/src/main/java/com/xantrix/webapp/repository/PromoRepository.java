package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<Promo, String>, JpaSpecificationExecutor<Promo> {

    Promo findByAnnoAndCodice(int anno, String codice);

    @Query(value = "select distinct o from Promo o" +
            " left join fetch o.dettPromos dd" +
            " left join fetch dd.tipoPromo" +
            " where o.anno = year(current_date) and exists (" +
            " select d.id from DettPromo d where d.promo.idPromo = o.idPromo" +
            " and d.inizio <= current_date and (d.fine is null or d.fine >= current_date)" +
            ")")
    List<Promo> selPromoActive();

    Optional<Promo> findByCodice(String codice);

    Optional<Promo> findByAnnoAndCodice(Integer anno, String codice);
}