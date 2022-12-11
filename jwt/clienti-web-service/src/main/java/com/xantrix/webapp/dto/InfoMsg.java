package com.xantrix.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 8800215072699908840L;
    private LocalDate date;
    private String code;
    private String message;
}
