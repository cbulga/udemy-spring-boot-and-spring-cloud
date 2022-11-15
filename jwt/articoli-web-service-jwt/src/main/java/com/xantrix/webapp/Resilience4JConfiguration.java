package com.xantrix.webapp;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfiguration {

    public static final String CIRCUIT_BREAKER = "circuitbreaker";

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                // soglia in percentuale di fallimenti che determina l'apertura del circuito
                .failureRateThreshold(50F)
                // numero di chiamate di riferimento
                .slidingWindowSize(5)
                // tempo di durata dello stato di ciucuito aperto
                .waitDurationInOpenState(Duration.ofMillis(1000))
                // numero di chiamate in stato half-open
                .permittedNumberOfCallsInHalfOpenState(5)
                // se impiega piu' di un secondo viene conteggiato come lento
                .slowCallDurationThreshold(Duration.ofMillis(1000))
                // soglia in percentuale di chiamate lente che determina l'apertura del circuito
                // (quindi se ho che almeno il 50% delle chiamate sono piu' lente di un secondo, il circuito
                // sarà aperto escludendo questo servizio temporaneamente)
                .slowCallRateThreshold(50.0F)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                // se il servizio non risponde entro 3 secondi dalla chiamata, allora il tentativo sara' classificato come fallito
                .timeoutDuration(Duration.ofMillis(3000))
                .cancelRunningFuture(true)
                .build();

        return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig)
                .build(), CIRCUIT_BREAKER);
    }
}
