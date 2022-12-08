package com.xantrix.webapp.dtos;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DettListiniDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9053886648628317606L;
    @EqualsAndHashCode.Include
    private Integer id;
    @Size(min = 5, max = 20, message = "{Size.DettListini.codArt.Validation}")
    @NotNull(message = "{NotNull.DettListini.codArt.Validation}")
    private String codArt;
    @Min(value = (long) 0.01, message = "{Min.DettListini.prezzo.Validation}")
    private Double prezzo;

    @SuppressWarnings("unused")
    public DettListiniDTO(String codArt, Double prezzo) {
        this.codArt = codArt;
        this.prezzo = prezzo;
    }
}
