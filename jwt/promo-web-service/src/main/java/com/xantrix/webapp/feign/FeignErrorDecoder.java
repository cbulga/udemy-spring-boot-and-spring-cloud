package com.xantrix.webapp.feign;

import com.xantrix.webapp.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                log.warn("Codice stato {}, metodo {}", response.status(), methodKey);
                return new Exception(response.reason());
            case 404:
                log.warn("Error occorso nel Feign client inviando una richiesta HTTP. Codice stato {}, metodo {}", response.status(), methodKey);
                return new NotFoundException("Prezzo articolo non trovato!");
            default:
                return new Exception(response.reason());
        }
    }
}
