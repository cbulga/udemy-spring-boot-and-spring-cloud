package com.xantrix.webapp.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class similar to the {@link CreatePromoDTO} and {@link UpdatePromoDTO}.<p>
 * Differences between {@link PromoDTO}, {@link CreatePromoDTO}, {@link UpdatePromoDTO}:<ul>
 *     <li>{@link PromoDTO}: <ul>
 *         <li>has a required {@link PromoDTO#getIdPromo() getIdPromo()} property</li>
 *         <li>has a {@link PromoDTO#getDettPromos() getDettPromos()} of type {@link DettPromoDTO} property</li>
 *     </ul></li>
 *     <li>{@link CreatePromoDTO}: <ul>
 *         <li>has a not required {@link CreatePromoDTO#getIdPromo() getIdPromo()} property</li>
 *         <li>has a {@link CreatePromoDTO#getDettPromos() getDettPromos()} of type {@link CreateOrUpdateDettPromoDTO} property</li>
 *     </ul></li>
 *     <li>{@link UpdatePromoDTO}: <ul>
 *         <li>has a required {@link UpdatePromoDTO#getIdPromo() getIdPromo()} property</li>
 *         <li>has a {@link UpdatePromoDTO#getDettPromos() getDettPromos()} of type {@link CreateOrUpdateDettPromoDTO} property</li>
 *     </ul></li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CreatePromoDTO implements IPromoDTO, Serializable {

    @Serial
    private static final long serialVersionUID = -520570552784824914L;
    // the difference with PromoDTO is that the idPromo can be null for a new promo that must be inserted
    @EqualsAndHashCode.Include
    private String idPromo;
    @NotNull(message = "{NotNull.Promo.anno.Validation}")
    private Integer anno;
    @NotNull(message = "{NotNull.Promo.codice.Validation}")
    @Size(message = "{Size.Promo.codice.Validation}", min = 3, max = 10)
    private String codice;
    @NotEmpty(message = "{NotNull.Promo.descrizione.Validation}")
    private String descrizione;
    @Builder.Default
    @Valid
    private List<CreateOrUpdateDettPromoDTO> dettPromos = new ArrayList<>();
    @Builder.Default
    @Valid
    private List<DepRifPromoDTO> depRifPromos = new ArrayList<>();
}
