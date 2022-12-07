package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.CreatePromoDTO;
import com.xantrix.webapp.dto.PromoDTO;
import com.xantrix.webapp.dto.UpdatePromoDTO;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;

import java.util.List;

public interface PromoService {

    void cleanCaches();

    PromoDTO createPromo(CreatePromoDTO promoDto, String authHeader) throws DuplicateException, NotFoundException;

    PromoDTO updatePromo(UpdatePromoDTO promoDto, String authHeader) throws NotFoundException;

    PromoDTO findById(String idPromo, String authHeader) throws NotFoundException;

    List<PromoDTO> findAllOrderByIdPromo(String authHeader);

    PromoDTO findByAnnoAndCodice(Integer anno, String codice, String authHeader) throws NotFoundException;

    List<PromoDTO> findByActive(String authHeader);

    PromoDTO deleteByIdPromo(String idPromo, String authHeader) throws NotFoundException;

    List<PromoDTO> deleteAll(String authHeader);
}
