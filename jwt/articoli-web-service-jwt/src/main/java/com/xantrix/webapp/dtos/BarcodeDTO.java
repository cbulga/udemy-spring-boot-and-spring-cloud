package com.xantrix.webapp.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BarcodeDTO implements Serializable {

    private static final long serialVersionUID = -7885944990935907091L;
    @EqualsAndHashCode.Include
    private String barcode;
    private String idTipoArt;
}
