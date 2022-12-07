package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entity.Articoli;

public interface ArticoliRepository extends JpaRepository<Articoli, String> {

    Articoli findByCodArt(String codArt);

    List<Articoli> findByDescrizioneLike(String descrizione);

    List<Articoli> findByDescrizioneLike(String descrizione, Pageable pageable);

    //JPQL
    @Query(value = "SELECT a FROM Articoli a JOIN a.barcode b WHERE b.barcode IN (:ean)")
    Articoli selByEan(@Param("ean") String ean);

    long count();

	/*
	//SQL
	@Query(value = "SELECT * FROM articoli a JOIN barcode b ON a.codart = b.codart WHERE b.barcode = :ean", nativeQuery = true )
	Articoli SelByEan(@Param("ean") String ean);
	*/
}
