package com.xantrix.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticoliDTO implements Serializable {

    private static final long serialVersionUID = -5193838648780749601L;
    private String codArt;
    private String descrizione;
    private String um;
    private String codStat;
    private int pzCart;
    private double pesoNetto;
    private String idStatoArt;
    private Date dataCreazione;
    private double prezzo = 0;
}
