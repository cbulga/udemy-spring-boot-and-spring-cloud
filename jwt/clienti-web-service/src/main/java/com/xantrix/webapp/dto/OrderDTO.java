package com.xantrix.webapp.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
public class OrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4975855727612977915L;
    private String id;
    private LocalDate data;
    private double totale;
    private String idCliente;
    private String fonte;
}