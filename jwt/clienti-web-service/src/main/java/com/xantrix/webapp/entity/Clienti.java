package com.xantrix.webapp.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "clienti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Clienti implements Serializable {

    @Serial
    private static final long serialVersionUID = -5313384138815258194L;
    @Id
    @Column(name = "codfidelity", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String codice;
    @Column(name = "nome")
    private String nome;
    @Column(name = "cognome", unique = true)
    private String cognome;
    @Column(name = "indirizzo")
    private String indirizzo;
    @Column(name = "comune")
    private String comune;
    @Column(name = "cap")
    private String cap;
    @Column(name = "prov")
    private String prov;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "mail")
    private String mail;
    @Column(name = "stato")
    private String stato;
    @Column(name = "datacreaz")
    private LocalDate dataCreaz;
    @OneToOne(mappedBy = "clienti", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cards card;
}
