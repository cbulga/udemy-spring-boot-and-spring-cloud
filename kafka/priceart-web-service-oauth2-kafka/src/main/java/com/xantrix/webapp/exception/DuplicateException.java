package com.xantrix.webapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DuplicateException extends Exception {

    private static final long serialVersionUID = 1L;
    private String messaggio;

    public DuplicateException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
    }
}
