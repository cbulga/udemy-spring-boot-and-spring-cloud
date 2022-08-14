package com.xantrix.webapp.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class BarcodeDto implements Serializable {

    private static final long serialVersionUID = -7885944990935907091L;
    private String barcode;
    private String idTipoArt;
}
