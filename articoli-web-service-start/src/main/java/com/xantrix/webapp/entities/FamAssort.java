package com.xantrix.webapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "famassort")
@Data
@ToString(exclude = {"articoli"})
public class FamAssort implements Serializable {
    private static final long serialVersionUID = 3788120361600509595L;

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "famAssort")
    @JsonBackReference
    private Set<Articoli> articoli = new HashSet<>();

}
