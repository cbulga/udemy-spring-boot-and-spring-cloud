package com.xantrix.webapp.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CardsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3493906286141771368L;
    @EqualsAndHashCode.Include
    private String codice;
    private Integer bollini;
    private LocalDate ultimaSpesa;
    private String obsoleto;
}
