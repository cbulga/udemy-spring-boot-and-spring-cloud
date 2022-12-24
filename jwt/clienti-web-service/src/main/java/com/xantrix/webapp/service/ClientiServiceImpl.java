package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.ClientiDTO;
import com.xantrix.webapp.dto.CreateClientiDTO;
import com.xantrix.webapp.entity.Cards;
import com.xantrix.webapp.entity.Clienti;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.repository.ClientiRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ClientiServiceImpl implements ClientiService {

    public static final String CLIENTE_DUPLICATO = "Cliente %s presente in anagrafica!";
    public static final String VERIFICHIAMO_LA_PRESENZA_DEL_CLIENTE_PASSATO = "Verifichiamo la presenza del cliente passato";
    public static final String NOT_FOUND_CLIENTI_BY_CODICE = "NotFound.Clienti.codice";
    static final String RETURN_VALUE = "return value is {}";
    private final ClientiRepository clientiRepository;
    private final ModelMapper modelMapper;
    private final ResourceBundleMessageSource errMessage;
    private final CacheManager cacheManager;

    public ClientiServiceImpl(ClientiRepository clientiRepository, ModelMapper modelMapper, ResourceBundleMessageSource errMessage, CacheManager cacheManager) {
        this.clientiRepository = clientiRepository;
        this.modelMapper = modelMapper;
        this.errMessage = errMessage;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(value = "clientiByCodice", key = "#codice", unless = "#result == null")
    public ClientiDTO findByCodice(String codice) throws NotFoundException {
        log.debug("findByCodice(codice={})", codice);
        ClientiDTO result = clientiRepository.findByCodice(codice)
                .map(this::createClientiDTO)
                .orElseThrow(() -> new NotFoundException(errMessage.getMessage(NOT_FOUND_CLIENTI_BY_CODICE, new String[]{codice},
                        LocaleContextHolder.getLocale())));
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    public void cleanCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            log.debug("Evicting cache {}", cacheName);
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        });
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clientiByCodice", key = "#codice"),
            @CacheEvict(value = "clientiByCognome", allEntries = true)
    })
    public void updateBolliniByCodice(Integer bollini, String codice) throws NotFoundException {
        log.debug("updateBolliniByCodice(bollini={}, codice={})", bollini, codice);
        int result = clientiRepository.updateBolliniByCodice(bollini, codice);
        if (result == 0)
            throw new NotFoundException(errMessage.getMessage(NOT_FOUND_CLIENTI_BY_CODICE, new String[]{codice},
                    LocaleContextHolder.getLocale()));
    }

    @Override
    @Cacheable(value = "clientiByCognome", key = "#cognome", sync = true)
    public List<ClientiDTO> findByCognome(String cognome) {
        List<ClientiDTO> result = clientiRepository.findByCognomeLike(cognome)
                .stream().map(this::createClientiDTO)
                .toList();
        log.trace(RETURN_VALUE, result);
        return result;
    }

    @Override
    @Transactional
    public ClientiDTO createClienti(CreateClientiDTO createClientiDTO) throws DuplicateException {
        log.debug("createClienti(createClientiDTO={})", createClientiDTO);
        ClientiDTO result;
        log.debug(VERIFICHIAMO_LA_PRESENZA_DEL_CLIENTE_PASSATO);
        if (clientiRepository.findByCodice(createClientiDTO.getCodice()).isPresent()) {
            String errorMessage = String.format(CLIENTE_DUPLICATO, createClientiDTO.getCodice());
            log.warn(errorMessage);
            throw new DuplicateException(errorMessage);
        }
        Clienti clienti = Clienti.builder()
                .codice(createClientiDTO.getCodice())
                .nome(createClientiDTO.getNome())
                .cognome(createClientiDTO.getCognome())
                .indirizzo(createClientiDTO.getIndirizzo())
                .comune(createClientiDTO.getComune())
                .cap(createClientiDTO.getCap())
                .prov(createClientiDTO.getProv())
                .telefono(createClientiDTO.getTelefono())
                .mail(createClientiDTO.getMail())
                .dataCreaz(LocalDate.now())
                .stato("1")
                .card(Cards.builder()
                        .codice(createClientiDTO.getCodice())
                        .bollini(createClientiDTO.getCard().getBollini())
                        .ultimaSpesa(createClientiDTO.getCard().getUltimaSpesa())
                        .obsoleto(createClientiDTO.getCard().getObsoleto())
                        .build())
                .build();
        clienti.getCard().setClienti(clienti);
        clienti = clientiRepository.save(clienti);
        result = createClientiDTO(clienti);
        log.trace(RETURN_VALUE, result);
        return result;
    }

    protected ClientiDTO createClientiDTO(Clienti clienti) {
        if (clienti == null) return null;
//        return modelMapper.map(clienti, ClientiDTO.class);
        return ClientiDTO.builder()
                .codice(clienti.getCodice())
                .nominativo(String.format("%s %s", StringUtils.defaultString(clienti.getNome()), StringUtils.defaultString(clienti.getCognome())))
                .indirizzo(clienti.getIndirizzo())
                .comune(clienti.getComune())
                .cap(clienti.getCap())
                .telefono(clienti.getTelefono())
                .mail(clienti.getMail())
                .stato(clienti.getStato())
                .bollini(Optional.ofNullable(clienti.getCard()).map(Cards::getBollini).orElse(null))
                .ultimaSpesa(Optional.ofNullable(clienti.getCard()).map(Cards::getUltimaSpesa).orElse(null))
                .build();
    }
}