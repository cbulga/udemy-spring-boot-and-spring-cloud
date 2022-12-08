package com.xantrix.webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoMsg implements Serializable {

    private static final long serialVersionUID = 5568242830756465544L;
    private LocalDate date;
    private String code;
    private String message;
}
