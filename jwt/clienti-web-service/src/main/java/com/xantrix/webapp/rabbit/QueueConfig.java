package com.xantrix.webapp.rabbit;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class QueueConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    private final AmqpAdmin amqpAdmin;

    public QueueConfig(RabbitMQProperties rabbitMQProperties, AmqpAdmin amqpAdmin) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void createQueues() {
        // creazione coda alphashop.order.queue su RabbitMQ
        amqpAdmin.declareQueue(new Queue(rabbitMQProperties.getQueueName(), true));
    }
}
