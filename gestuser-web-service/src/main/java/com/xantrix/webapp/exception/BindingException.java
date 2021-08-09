package com.xantrix.webapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BindingException extends Exception {

    private static final long serialVersionUID = -1646083143194195402L;
    private String messaggio;

    public BindingException(String message) {
        super(message);
        this.messaggio = message;
    }
}

