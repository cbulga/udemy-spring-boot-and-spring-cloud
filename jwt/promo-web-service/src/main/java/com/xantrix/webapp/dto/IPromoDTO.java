package com.xantrix.webapp.dto;

import java.util.List;

public interface IPromoDTO {

    String getIdPromo();
    Integer getAnno();
    String getCodice();
    String getDescrizione();
    List<? extends IDettPromoDTO> getDettPromos();
    List<DepRifPromoDTO> getDepRifPromos();
}
