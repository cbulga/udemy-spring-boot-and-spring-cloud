package com.xantrix.webapp.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class InfoMsg {

    private LocalDate data;
    private String code;
    private String message;
}
