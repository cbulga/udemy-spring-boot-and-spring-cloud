package com.xantrix.webapp.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class OrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2903256420097453046L;
    private String id;
    private LocalDate data;
    private double totale;
    private String idCliente;
    private String fonte;
}
