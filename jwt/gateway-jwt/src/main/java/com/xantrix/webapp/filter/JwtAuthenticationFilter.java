package com.xantrix.webapp.filter;

import com.xantrix.webapp.exception.JwtTokenMalformedException;
import com.xantrix.webapp.exception.JwtTokenMissingException;
import com.xantrix.webapp.util.JwtUtil;
import lombok.extern.java.Log;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
@Log
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final List<String> ADMIN_ENDPOINTS = List.of("/api/prezzi/elimina",
            "/api/listino/inserisci",
            "/api/listino/elimina",
            "/api/articoli/inserisci",
            "/api/articoli/modifica",
            "/api/articoli/elimina",
            "/api/articoli-cache/clearAllCaches",
            "/api/prezzi/elimina",
            "/api/listino/inserisci",
            "/api/listino/elimina",
            "/actuator/gateway/**",
            "/api/promo/elimina",
            "/api/promo/inserisci",
            "/api/promo/aggiorna",
            "/api/promo-cache/clearAllCaches",
            "/api/prezzi-cache/clearAllCaches");
    private static final List<String> USER_ENDPOINTS = List.of("/api/articoli/cerca/ean",
            "/api/articoli/cerca/codice",
            "/api/articoli/cerca/descrizione",
            "/api/prezzi",
            "/api/prezzi/info",
            "/api/listino/cerca/id",
            "/api/promo/prezzo/**",
            "/api/promo/id/**",
            "/api/promo/codice",
            "/api/promo/**",
            "/api/promo",
            "/api/promo/active");
    private static final Predicate<ServerHttpRequest> IS_ADMIN_ENDPOINT = r -> ADMIN_ENDPOINTS.stream()
            .noneMatch(uri -> r.getURI().getPath().contains(uri));
    private static final Predicate<ServerHttpRequest> IS_USER_ENDPOINT = r -> USER_ENDPOINTS.stream()
            .noneMatch(uri -> r.getURI().getPath().contains(uri));
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        boolean isAdmin = !IS_ADMIN_ENDPOINT.test(request);
        boolean isUser = !IS_USER_ENDPOINT.test(request);

        if ((isAdmin || isUser) && !request.getHeaders().containsKey("Authorization")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        if (isUser || isAdmin) {
            final String token = request.getHeaders().getOrEmpty("Authorization").get(0).replace("Bearer ", "");

            try {
                jwtUtil.validateToken(token);
            } catch (JwtTokenMalformedException | JwtTokenMissingException e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }

            String user = jwtUtil.getUsernameFromToken(token);
            log.info(user);

            List<String> authorities = jwtUtil.getAuthFromToken(token);

            if (isAdmin && !authorities.contains("ROLE_ADMIN")
                    || isUser && !authorities.contains("ROLE_USER")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return response.setComplete();
            }

            /*
            exchange.getRequest().mutate().header("id", String.valueOf(claims.get("id"))).build();
            */
        }

        return chain.filter(exchange);
    }
}
