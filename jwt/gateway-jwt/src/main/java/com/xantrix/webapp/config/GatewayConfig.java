package com.xantrix.webapp.config;

import com.xantrix.webapp.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("articoliModule", r -> r
                        .path("/api/articoli/**")
                        //.and().method("GET")
                        .filters(f -> f.circuitBreaker(config -> config
                                        .setName("articoliCircuitBreaker")
                                        .setFallbackUri("forward:/articolo-fallback"))
                                .filter(jwtAuthFilter))
                        .uri("lb://ProductsWebService"))
                .route("prezziModule", r -> r
                        .path("/api/prezzi/**")
                        //.and().method("GET,POST,DELETE")
                        .filters(f -> f.circuitBreaker(config -> config
                                        .setName("prezziCircuitBreaker")
                                        .setFallbackUri("forward:/prezzo-fallback"))
                                .filter(jwtAuthFilter))
                        .uri("lb://PriceArtWebService"))
                .route("listinoModule", r -> r
                        .path("/api/listino/**")
                        //.and().method("GET")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://PriceArtWebService"))
                .route("authModule", r -> r.path("/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .build();
    }
}
