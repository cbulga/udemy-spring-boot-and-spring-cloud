package com.xantrix.webapp.service;

import com.xantrix.webapp.dto.OrderDTO;
import com.xantrix.webapp.rabbit.RabbitMQProperties;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitOrderMessagingService implements OrderMessagingService {

    private final RabbitTemplate rabbit;

    private final RabbitMQProperties rabbitMQProperties;

    @Autowired
    public RabbitOrderMessagingService(RabbitTemplate rabbit, RabbitMQProperties rabbitMQProperties) {
        this.rabbit = rabbit;
        this.rabbitMQProperties = rabbitMQProperties;
    }
	
	/*
	public void sendOrder(OrderDTO ordine) 
	{
		MessageConverter converter = rabbit.getMessageConverter();
		MessageProperties props = new MessageProperties();
		Message message = converter.toMessage(ordine, props);
		
		rabbit.send("alphashop.orders", message);
	}
	**/

    public void sendOrder(OrderDTO ordine) {
        rabbit.convertAndSend(rabbitMQProperties.getQueueName(), ordine,
                message -> {
                    MessageProperties props = message.getMessageProperties();
                    props.setHeader("X_ORDER_SOURCE", ordine.getFonte());

                    return message;
                });
    }
}
