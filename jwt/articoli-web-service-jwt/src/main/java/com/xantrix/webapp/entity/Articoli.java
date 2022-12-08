package com.xantrix.webapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.xantrix.webapp.validation.CodArt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ARTICOLI")
@Data
@NoArgsConstructor
@AllArgsConstructor // Costruttore con tutti i parametri
@Builder
public class Articoli implements Serializable {

    @Serial
    private static final long serialVersionUID = 7361753083273455478L;
    @Id
    @Column(name = "CODART")
    @Size(min = 5, max = 20, message = "{Size.Articoli.codArt.Validation}")
    @NotNull(message = "{NotNull.Articoli.codArt.Validation}")
    @CodArt
    private String codArt;

    @Column(name = "DESCRIZIONE")
    @Size(min = 6, max = 80, message = "{Size.Articoli.descrizione.Validation}")
    private String descrizione;

    @Column(name = "UM")
    private String um;

    @Column(name = "CODSTAT")
    @NotBlank(message = "{NotBlank.Articoli.codStat.Validation}")
    private String codStat;

    @Column(name = "PZCART")
    @Max(value = 99, message = "{Max.Articoli.pzCart.Validation}")
    private Integer pzCart;

    @Column(name = "PESONETTO")
    @Min(value = (long) 0.01, message = "{Min.Articoli.pesoNetto.Validation}")
    private double pesoNetto;

    @Column(name = "IDSTATOART")
    @NotNull(message = "{NotNull.Articoli.idStatoArt.Validation}")
    private String idStatoArt;

    @Column(name = "DATACREAZIONE")
    private LocalDate dataCreaz;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "articolo", orphanRemoval = true)
    @JsonManagedReference
    private Set<Barcode> barcode = new HashSet<>();

    @OneToOne(mappedBy = "articolo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ingredienti ingredienti;

    @ManyToOne
    @JoinColumn(name = "IDFAMASS", referencedColumnName = "ID")
    private FamAssort famAssort;

    @ManyToOne
    @JoinColumn(name = "IDIVA", referencedColumnName = "idIva")
    private Iva iva;

    public Articoli(String codArt, String descrizione, Integer pzCart, Double pesoNetto, String idStatoArt) {
        this.codArt = codArt;
        this.descrizione = descrizione;
        this.pzCart = pzCart;
        this.pesoNetto = pesoNetto;
        this.idStatoArt = idStatoArt;
    }

}
