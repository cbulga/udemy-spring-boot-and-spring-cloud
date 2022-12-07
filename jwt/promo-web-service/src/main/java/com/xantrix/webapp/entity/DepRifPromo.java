package com.xantrix.webapp.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "deprifpromo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DepRifPromo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1807339901735627291L;
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    @NotNull(message = "{}")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpromo", referencedColumnName = "idpromo", nullable = false)
    @NotNull(message = "{NotNull.DepRifPromo.promo.Validation}")
    private Promo promo;

    @Column(name = "iddeposito", nullable = false)
    @NotNull(message = "{NotNull.DepRifPromo.idDeposito.Validation}")
    private Long idDeposito;
}
