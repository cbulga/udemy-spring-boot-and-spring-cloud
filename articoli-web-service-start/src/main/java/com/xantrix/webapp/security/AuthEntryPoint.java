package com.xantrix.webapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {

    private static final String REALM = "REAME";
    protected static final String AUTHENTICATION_ERROR_MESSAGE = "User ID e/o password errati";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("Errore sicurezza: {}", authException.getMessage());

        // authentication failed, send error response
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic-realm=" + getRealmName());

        PrintWriter writer = response.getWriter();
        writer.println(AUTHENTICATION_ERROR_MESSAGE);
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName(REALM);
        super.afterPropertiesSet();
    }
}
