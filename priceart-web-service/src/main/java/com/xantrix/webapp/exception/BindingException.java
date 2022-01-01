package com.xantrix.webapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BindingException extends Exception {

    private static final long serialVersionUID = 1L;
    private String messaggio;

    public BindingException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
    }
}
