package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.Listini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListinoRepository extends JpaRepository<Listini, String> {

    @Query(value = "SELECT COUNT(*) as Qta FROM Listini a JOIN dettListini b ON a.id = b.idlist WHERE a.id = :listinoId", nativeQuery = true)
    Long getQtaDettList(@Param("listinoId") String listinoId);
}
