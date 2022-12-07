package com.xantrix.webapp.dto;

import java.time.LocalDate;

public interface IDettPromoDTO {

    Long getId();

    Integer getRiga();

    String getCodArt();

    String getCodFid();

    LocalDate getInizio();

    LocalDate getFine();

    String getOggetto();

    String getIsFid();

    String getIdTipoPromo();
}
