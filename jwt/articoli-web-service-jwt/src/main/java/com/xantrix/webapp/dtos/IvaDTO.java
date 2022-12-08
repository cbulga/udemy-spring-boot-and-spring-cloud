package com.xantrix.webapp.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IvaDTO implements Serializable {

    private static final long serialVersionUID = 7970056696778029108L;
    @EqualsAndHashCode.Include
    private int idIva;
    private String descrizione;
    private int aliquota;
}
