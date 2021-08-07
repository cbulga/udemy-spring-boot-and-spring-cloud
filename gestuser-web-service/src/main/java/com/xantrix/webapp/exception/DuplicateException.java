package com.xantrix.webapp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DuplicateException extends Exception {
    private static final long serialVersionUID = 6671877602598802506L;
    private String messaggio;
}
