package com.xantrix.webapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String ELEMENTO_RICERCATO_NON_TROVATO = "Elemento Ricercato Non Trovato!";
    private final String messaggio;

    public NotFoundException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
    }

    @SuppressWarnings("unused")
    public NotFoundException() {
        this(ELEMENTO_RICERCATO_NON_TROVATO);
    }
}
