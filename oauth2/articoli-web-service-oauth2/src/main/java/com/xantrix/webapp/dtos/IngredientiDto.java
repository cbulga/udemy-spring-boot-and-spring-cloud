package com.xantrix.webapp.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class IngredientiDto implements Serializable {

    private static final long serialVersionUID = 6256735220690390964L;
    private String codArt;
    private String info;
}
