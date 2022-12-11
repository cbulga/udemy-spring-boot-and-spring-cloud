package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.Clienti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientiRepository extends JpaRepository<Clienti, String> {

    @Query(value = "select o from Clienti o left join fetch o.card where o.codice = :codice")
    Optional<Clienti> findByCodice(@Param(value = "codice") String codice);

    @Query(value = "update Cards o set o.bollini = o.bollini + :bollini, o.ultimaSpesa = current_date where o.clienti.codice = :codice")
    @Modifying
    int updateBolliniByCodice(@Param("bollini") Integer bollini, @Param("codice") String codice);

    List<Clienti> findByCognomeLike(String cognome);
}
