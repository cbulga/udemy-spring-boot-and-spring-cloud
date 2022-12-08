package com.xantrix.webapp.dtos;

import lombok.Data;

@Data
public class PrezzoDTO {
    private String codArt;
    private double prezzo = 0;
    private double sconto = 0;
    private int tipo;
}
