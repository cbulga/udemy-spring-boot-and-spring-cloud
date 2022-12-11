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
public class ClientiDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6701928785462050994L;
    private String codice;
    private String nominativo;
    private String indirizzo;
    private String comune;
    private String cap;
    private String telefono;
    private String mail;
    private String stato;
    private int bollini;
    private LocalDate ultimaSpesa;
}
