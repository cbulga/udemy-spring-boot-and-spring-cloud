package com.xantrix.webapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends Exception {

    private static final long serialVersionUID = -8729169303699924451L;
    private String messaggio = "Elemento Ricercato Non Trovato!";
}
