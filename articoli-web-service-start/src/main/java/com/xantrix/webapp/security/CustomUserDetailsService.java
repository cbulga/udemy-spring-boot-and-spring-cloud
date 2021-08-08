package com.xantrix.webapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    public static final String NOME_UTENTE_ASSENTE_O_NON_VALIDO = "Nome utente assente o non valido";
    public static final String CONNESSIONE_AL_SERVIZIO_DI_AUTENTICAZIONE_NON_RIUSCITA = "Connessione al servizio di autenticazione non riuscita!";
    public static final String UTENTE_NON_TROVATO = "Utente %s non trovato!";
    public static final String ATTIVO_TRUE_VALUE = "SI";

    private final UserConfig config;

    public CustomUserDetailsService(UserConfig config) {
        this.config = config;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if (userId == null || userId.length() < 2) {
            log.warn(NOME_UTENTE_ASSENTE_O_NON_VALIDO);
            throw new UsernameNotFoundException(NOME_UTENTE_ASSENTE_O_NON_VALIDO);
        }
        Utenti utente = getHttpValue(userId);
        if (utente == null) {
            String errorMessage = String.format(UTENTE_NON_TROVATO, userId);
            log.warn(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        return User.withUsername(utente.getUserId())
                .disabled(!utente.getAttivo().equalsIgnoreCase(ATTIVO_TRUE_VALUE))
                .password(utente.getPassword())
//                .roles(utente.getRuoli().toArray(new String[0]))
                .authorities(utente.getRuoli().stream()
                        .map(r -> "ROLE_" + r)
                        .toArray(String[]::new))
                .build();
    }

    private Utenti getHttpValue(String userId) {
        URI url;
        try {
            url = new URI(config.getSrvUrl() + userId);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Error using the gestUser ws url", ex);
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(config.getUserId(), config.getPassword()));

        try {
            return restTemplate.getForObject(url, Utenti.class);
        } catch (RestClientException ex) {
            log.warn(CONNESSIONE_AL_SERVIZIO_DI_AUTENTICAZIONE_NON_RIUSCITA, ex);
        }
        return null;
    }
}
