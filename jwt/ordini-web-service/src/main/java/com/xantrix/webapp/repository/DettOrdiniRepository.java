package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.DettOrdini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DettOrdiniRepository extends JpaRepository<DettOrdini, Long> {

    @Query(value = "SELECT SUM(o.qta * o.prezzo) FROM DettOrdini o WHERE o.ordine.id = :id")
    Double getValOrdine(@Param("id") String ordineId);
}
