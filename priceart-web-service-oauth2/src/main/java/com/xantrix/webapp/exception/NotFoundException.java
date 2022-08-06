package com.xantrix.webapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private String messaggio = "Elemento Ricercato Non Trovato!";

    public NotFoundException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
    }
}
