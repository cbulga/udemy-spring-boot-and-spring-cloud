package com.xantrix.webapp.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorResponse {

    private Date data = new Date();
    private int codice;
    private String messaggio;
}
