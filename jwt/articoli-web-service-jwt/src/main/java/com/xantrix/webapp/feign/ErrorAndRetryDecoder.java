package com.xantrix.webapp.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ErrorAndRetryDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            switch (response.status()) {
                case 400:
                    log.warn("Codice Stato {}, Metodo = {}", response.status(), methodKey);
                    break;
                case 404: {
                    log.warn("Errore occorso nel Feign client inviando una Richiesta HTTP. Codice Stato: {}, Metodo = {}", response.status(), methodKey);

                    //return new NotFoundException("Prezzo Articolo Non Trovato!");
                    break;
                }
            }
        } else if (response.status() >= 500) {
            log.warn("Codice Stato {}, Metodo = {}", response.status(), methodKey);
            //return new RetryableException();
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
