package com.xantrix.webapp.controller;

import com.xantrix.webapp.exception.JwtTokenMalformedException;
import com.xantrix.webapp.exception.JwtTokenMissingException;
import com.xantrix.webapp.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    public static final String AUTHORIZATION = "Authorization";
    private final JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(value = "/auth")
    public Mono<String> testAuth(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        if (headers.get(AUTHORIZATION) == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.just("Token assente");
        } else {
            final String token = headers.get(AUTHORIZATION).get(0).replace("Bearer ", "");

            try {
                jwtUtil.validateToken(token);
            } catch (JwtTokenMalformedException | JwtTokenMissingException e) {
                log.warn(e.getMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return Mono.just("Token errato o scaduto");
            }
        }

        return Mono.just("Test connessione ok");
    }
}
