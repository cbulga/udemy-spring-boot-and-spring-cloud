package com.xantrix.webapp.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IngredientiDTO implements Serializable {

    private static final long serialVersionUID = 6256735220690390964L;
    @EqualsAndHashCode.Include
    private String codArt;
    private String info;
}
