package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "IVA")
@Data
@ToString(exclude = {"articoli"})
public class Iva {
    @Id
    @Column(name = "IDIVA")
    private int idIva;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "ALIQUOTA")
    private int aliquota;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "iva")
    //manca il parametro cascade perchè l'accesso sarà di sola lettura
    @JsonBackReference
    private Set<Articoli> articoli = new HashSet<>();


}
