package com.xantrix.webapp.dtos;

import com.xantrix.webapp.validation.CodArt;
import lombok.*;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArticoliDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3923763205922073780L;
    @NotNull(message = "{NotNull.Articoli.codArt.Validation}")
    @CodArt
    @EqualsAndHashCode.Include
    private String codArt;
    @Size(min = 6, max = 80, message = "{Size.Articoli.descrizione.Validation}")
    private String descrizione;
    private String um;
    @NotBlank(message = "{NotBlank.Articoli.codStat.Validation}")
    private String codStat;
    @Max(value = 99, message = "{Max.Articoli.pzCart.Validation}")
    private int pzCart;
    @Min(value = (long) 0.01, message = "{Min.Articoli.pesoNetto.Validation}")
    private double pesoNetto;
    @NotNull(message = "{NotNull.Articoli.idStatoArt.Validation}")
    private String idStatoArt;
    private LocalDate dataCreazione;
    private double prezzo = 0;
    private double prezzoPromo = 0;
    @Builder.Default
    private Set<BarcodeDTO> barcode = new HashSet<>();
    private IngredientiDTO ingredienti;
    private CategoriaDTO famAssort;
    private IvaDTO iva;
}
