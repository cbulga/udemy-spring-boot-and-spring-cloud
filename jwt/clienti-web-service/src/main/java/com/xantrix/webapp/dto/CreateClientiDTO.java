package com.xantrix.webapp.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CreateClientiDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7188469348429774127L;
    @EqualsAndHashCode.Include
    private String codice;
    private String nome;
    private String cognome;
    private String indirizzo;
    private String comune;
    private String cap;
    private String prov;
    private String telefono;
    private String mail;
    private CardsDTO card;
}
