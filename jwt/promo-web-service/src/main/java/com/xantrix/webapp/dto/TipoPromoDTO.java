package com.xantrix.webapp.dto;

import javax.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TipoPromoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8353193080849779637L;
    @NotNull(message = "{NotNull.TipoPromo.idTipoPromo.Validation}")
    private String idTipoPromo;
}
