package com.xantrix.webapp.rabbit;

import com.xantrix.webapp.dto.OrderDTO;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ClientiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderListener {
    private final ClientiService clientiService;

    private final FidelityProperties fidelityProperties;

    public OrderListener(ClientiService clientiService, FidelityProperties fidelityProperties) {
        this.clientiService = clientiService;
        this.fidelityProperties = fidelityProperties;
    }

    @RabbitListener(queues = "alphashop.order.queue")
    public void receiveOrder(OrderDTO ordine, @Header("X_ORDER_SOURCE") String source) {
        int bollini;

        log.info("Ricevuto Ordine {} di euro {}", ordine.getId(), ordine.getTotale());

        if (ordine.getIdCliente().length() > 0 && ordine.getTotale() >= fidelityProperties.getValminimo()) {
            log.info("***** Calcolo Bollini Cliente {} *****", ordine.getIdCliente());

            bollini = (int) ordine.getTotale() / fidelityProperties.getValbollino();

            if (source.equalsIgnoreCase("MOBILE")) {
                log.info("Fonte {}. Eseguo Bonus Bollini!", source);
                bollini *= fidelityProperties.getBonusmobile();
            }

            try {
                clientiService.updateBolliniByCodice(bollini, ordine.getIdCliente());
            } catch (NotFoundException e) {
                // cliente non trovato...
                log.error(e.getMessaggio(), e);
            }

            log.info("Modificato monte bollini fidelity {} di {} bollini", ordine.getIdCliente(), bollini);
        } else {
            log.warn("Impossibile modificare il monte bollini");
        }
    }
}
