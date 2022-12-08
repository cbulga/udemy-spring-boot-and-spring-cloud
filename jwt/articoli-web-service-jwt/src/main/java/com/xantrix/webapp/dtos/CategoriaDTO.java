package com.xantrix.webapp.dtos;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CategoriaDTO implements Serializable {

    private static final long serialVersionUID = 8090444846402357034L;
    @EqualsAndHashCode.Include
    private int id;
    private String descrizione;
}
