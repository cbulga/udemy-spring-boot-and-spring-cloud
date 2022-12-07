package com.xantrix.webapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "dettpromo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DettPromo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1522095631259230863L;
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "hibernate_sequence", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpromo", referencedColumnName = "idpromo", nullable = false)
    @EqualsAndHashCode.Include
    @NotNull(message = "{NotNull.DettPromo.promo.Validation}")
    private Promo promo;

    @Column(name = "riga", nullable = false)
    @NotNull(message = "{NotNull.DettPromo.riga.Validation}")
    private Integer riga;

    @Column(name = "codart", nullable = false)
    @EqualsAndHashCode.Include
    @NotNull(message = "{NotNull.DettPromo.codArt.Validation}")
    @Size(message = "{Size.DettPromo.codArt.Validation}", min = 5, max = 20)
    private String codArt;

    @Column(name = "codfid")
    private String codFid;

    @Column(name = "inizio")
    private LocalDate inizio;

    @Column(name = "fine")
    private LocalDate fine;

    @ManyToOne
    @JoinColumn(name = "idtipopromo", referencedColumnName = "idtipopromo", nullable = false)
    @NotNull(message = "{NotNull.DettPromo.tipoPromo.Validation}")
    private TipoPromo tipoPromo;

    @Column(name = "oggetto", nullable = false)
    @NotNull(message = "{NotNull.DettPromo.oggetto.Validation}")
    @NotEmpty(message = "{NotEmpty.DettPromo.oggetto.Validation}")
    private String oggetto;

    @Column(name = "isfid")
    private String isFid;
}
