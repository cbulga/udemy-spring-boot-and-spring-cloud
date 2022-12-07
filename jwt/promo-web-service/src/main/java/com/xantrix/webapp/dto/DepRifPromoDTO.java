package com.xantrix.webapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DepRifPromoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2699101948100729138L;
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "{NotNull.DepRifPromo.idDeposito.Validation}")
    private Long idDeposito;
}
