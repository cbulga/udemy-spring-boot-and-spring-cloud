package com.xantrix.webapp.configserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        String errMsg = "Priviligi Insufficenti. Impossibile Proseguire.";

        HttpStatus httpStatus = HttpStatus.FORBIDDEN; // 403
        response.setStatus(httpStatus.value());

        log.warn(errMsg);

        response.getOutputStream().println(objectMapper.writeValueAsString(errMsg));
    }
}
