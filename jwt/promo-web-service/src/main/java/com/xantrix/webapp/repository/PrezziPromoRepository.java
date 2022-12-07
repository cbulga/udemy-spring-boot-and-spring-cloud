package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.DettPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrezziPromoRepository extends JpaRepository<DettPromo, Long>, JpaSpecificationExecutor<DettPromo> {

    @Query(value = "select o from DettPromo o inner join fetch o.promo p" +
            " inner join fetch o.tipoPromo" +
            " where o.codArt = :codArt and o.isFid = 'No'" +
            " and p.anno = year(current_date)" +
            " and o.inizio <= current_date and (o.fine is null or o.fine >= current_date)")
    List<DettPromo> selByCodArtAndPromoAttiva(@Param("codArt") String codArt);

    @Query(value = "select o from DettPromo o inner join fetch o.promo p" +
            " inner join fetch o.tipoPromo" +
            " where o.codArt = :codArt and o.isFid = 'Si'" +
            " and p.anno = year(current_date)" +
            " and o.inizio <= current_date and (o.fine is null or o.fine >= current_date)")
    List<DettPromo> selByCodArtAndFidAndPromoAttiva(@Param("codArt") String codArt);

    @Query(value = "select o from DettPromo o inner join fetch o.promo p" +
            " inner join fetch o.tipoPromo" +
            " where o.codArt = :codArt and o.isFid = 'Si' and o.codFid = :codFid" +
            " and p.anno = year(current_date)" +
            " and o.inizio <= current_date and (o.fine is null or o.fine >= current_date)")
    List<DettPromo> selByCodArtAndCodFidAndPromoAttiva(@Param("codArt") String codArt, @Param("codFid") String codFid);
}