package com.xantrix.webapp.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tipopromo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TipoPromo implements Serializable {

    @Serial
    private static final long serialVersionUID = -329957439878351841L;
    @Id
    @Column(name = "idtipopromo", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    @NotNull(message = "{NotNull.TipoPromo.idTipoPromo.Validation}")
    private Integer idTipoPromo;

    @Column(name = "descrizione")
    private String descrizione;

    public TipoPromo(Integer idTipoPromo) {
        this.idTipoPromo = idTipoPromo;
    }
}
