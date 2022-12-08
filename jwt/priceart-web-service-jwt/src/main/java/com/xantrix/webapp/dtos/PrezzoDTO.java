package com.xantrix.webapp.dtos;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PrezzoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1362402364090664702L;
    @EqualsAndHashCode.Include
    private String codArt;
    private double prezzo = 0;
    private double sconto = 0;
    private int tipo;
}
