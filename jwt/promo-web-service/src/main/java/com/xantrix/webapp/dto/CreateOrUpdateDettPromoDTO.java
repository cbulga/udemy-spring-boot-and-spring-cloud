package com.xantrix.webapp.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Class similar to the {@link DettPromoDTO}.<p>
 * Differences between {@link DettPromoDTO} and {@link CreateOrUpdateDettPromoDTO}:<ul>
 *     <li>{@link DettPromoDTO}: <ul>
 *         <li>has a not required {@link DettPromoDTO#getId() getId()} property</li>
 *         <li>has a {@link DettPromoDTO#getIdTipoPromo() getIdTipoPromo()} property</li>
 *     </ul></li>
 *     <li>{@link CreateOrUpdateDettPromoDTO}: <ul>
 *         <li>has a not required {@link CreateOrUpdateDettPromoDTO#getId() getId()} property</li>
 *         <li>has a {@link CreateOrUpdateDettPromoDTO#getTipoPromo() getTipoPromo()} property</li>
 *     </ul></li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CreateOrUpdateDettPromoDTO implements IDettPromoDTO, Serializable {

    @Serial
    private static final long serialVersionUID = -3934645679899884043L;
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(message = "{NotNull.DettPromo.riga.Validation}")
    private Integer riga;
    @NotNull(message = "{NotNull.DettPromo.codArt.Validation}")
    @Size(message = "{Size.DettPromo.codArt.Validation}", min = 5, max = 20)
    private String codArt;
    private String codFid;
    private LocalDate inizio;
    private LocalDate fine;
    @NotNull(message = "{NotNull.DettPromo.oggetto.Validation}")
    @NotEmpty(message = "{NotEmpty.DettPromo.oggetto.Validation}")
    private String oggetto;
    private String isFid;
    @NotNull(message = "{NotNull.DettPromo.tipoPromo.Validation}")
    private TipoPromoDTO tipoPromo;

    @Override
    public String getIdTipoPromo() {
        return Optional.ofNullable(tipoPromo).map(TipoPromoDTO::getIdTipoPromo).orElse(null);
    }
}
