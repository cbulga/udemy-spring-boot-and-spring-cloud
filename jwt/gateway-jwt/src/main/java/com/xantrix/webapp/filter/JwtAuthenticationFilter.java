package com.xantrix.webapp.filter;


import com.xantrix.webapp.exception.JwtTokenMalformedException;
import com.xantrix.webapp.exception.JwtTokenMissingException;
import com.xantrix.webapp.util.JwtUtil;
import lombok.extern.java.Log;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
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
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        final List<String> adminEndpoints = List.of("/api/prezzi/elimina",
                "/api/listino/inserisci",
                "/api/listino/elimina",
                "/api/articoli/inserisci",
                "/api/articoli/modifica",
                "/api/articoli/elimina",
                "/actuator/gateway/**");

        Predicate<ServerHttpRequest> isAdminEndPoints = r -> adminEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));

        boolean isAdmin = !isAdminEndPoints.test(request);

        if (!request.getHeaders().containsKey("Authorization")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

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

        if (isAdmin && !authorities.contains("ROLE_ADMIN")) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);

            return response.setComplete();
        }

		/*
		exchange.getRequest().mutate().header("id", String.valueOf(claims.get("id"))).build();
		*/

        return chain.filter(exchange);
    }
}
