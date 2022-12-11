package com.xantrix.webapp.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cards implements Serializable {

    @Serial
    private static final long serialVersionUID = 3663585233403287667L;
    @Id
    @Column(name = "codfidelity", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private String codice;
    @Column(name = "bollini")
    private Integer bollini;
    @Column(name = "ultimaspesa")
    private LocalDate ultimaSpesa;
    @Column(name = "obsoleto")
    private String obsoleto;
    @OneToOne(optional = false)
    @JoinColumn(name = "codfidelity", referencedColumnName = "codfidelity", unique = true)
    @ToString.Exclude
    private Clienti clienti;
}
