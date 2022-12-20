package com.xantrix.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class WebEndpoints {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/articolo-fallback", request ->
                        ServerResponse.ok().body(Mono.just("fallback articolo attivato"), String.class))
                .GET("/prezzo-fallback", request ->
                        ServerResponse.ok().body(Mono.just("fallback prezzo attivato"), String.class))
                .build();
    }
}
