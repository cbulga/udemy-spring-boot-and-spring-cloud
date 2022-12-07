package com.xantrix.webapp.entity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "promo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Promo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1111470042301402049L;
    @Id
    @Column(name = "idpromo", nullable = false, updatable = false)
    @NotNull(message = "{NotNull.Promo.idPromo.Validation}")
    @EqualsAndHashCode.Include
    private String idPromo;

    @Column(name = "anno", nullable = false)
    @NotNull(message = "{NotNull.Promo.anno.Validation}")
    private Integer anno;

    @Column(name = "codice", nullable = false)
    @NotNull(message = "{NotNull.Promo.codice.Validation}")
    @Size(message = "{Size.Promo.codice.Validation}", min = 3, max = 10)
    private String codice;

    @Column(name = "descrizione")
    @NotEmpty(message = "{NotNull.Promo.descrizione.Validation}")
    private String descrizione;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promo", orphanRemoval = true)
    @Builder.Default
    @Valid
    private List<DettPromo> dettPromos = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promo", orphanRemoval = true)
    @Builder.Default
    @Valid
    private List<DepRifPromo> depRifPromos = new ArrayList<>();

    public Promo(String idPromo, Integer anno, String codice, String descrizione) {
        this.idPromo = idPromo;
        this.anno = anno;
        this.codice = codice;
        this.descrizione = descrizione;
    }

    @PrePersist
    public void newPromoAttempt() {
        log.info("Attempting to add new promo with description: " + descrizione);
        if (StringUtils.isBlank(this.idPromo))
            this.idPromo = UUID.randomUUID().toString();
    }
}
