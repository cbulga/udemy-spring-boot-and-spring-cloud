package com.xantrix.webapp.repository;

import com.xantrix.webapp.entity.Ordini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdiniRepository extends JpaRepository<Ordini, String> {
}
