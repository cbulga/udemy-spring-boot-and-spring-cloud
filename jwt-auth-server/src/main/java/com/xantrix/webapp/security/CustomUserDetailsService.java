package com.xantrix.webapp.security;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    protected static final String NOME_UTENTE_ASSENTE_O_NON_VALIDO = "Nome utente assente o non valido";
    public static final String UTENTE_NON_TROVATO = "Utente %s non Trovato!!";
    public static final String CONNESSIONE_AL_SERVIZIO_DI_AUTENTICAZIONE_NON_RIUSCITA = "Connessione al servizio di autenticazione non riuscita!!";

    @Autowired
    private UserConfig config;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        if (userId == null || userId.length() < 2) {
            log.warn(NOME_UTENTE_ASSENTE_O_NON_VALIDO);
            throw new UsernameNotFoundException(NOME_UTENTE_ASSENTE_O_NON_VALIDO);
        }
        Utenti utente = this.getHttpValue(userId);
        if (utente == null) {
            String errorMessage = String.format(UTENTE_NON_TROVATO, userId);
            log.warn(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }

        UserBuilder builder = null;
        builder = org.springframework.security.core.userdetails.User.withUsername(utente.getUserId());
        builder.disabled((utente.getAttivo().equals("Si") ? false : true));
        builder.password(utente.getPassword());

        String[] profili = utente.getRuoli().stream().map(a -> "ROLE_" + a).toArray(String[]::new);

        builder.authorities(profili);

        return builder.build();
    }

    private Utenti getHttpValue(String UserId) {
        URI url;
        try {
            url = new URI(config.getSrvUrl() + UserId);
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Error using the gestUser ws url", ex);
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(config.getUserId(), config.getPassword()));

        try {
            return restTemplate.getForObject(url, Utenti.class);
        } catch (RestClientException e) {
            log.warn(CONNESSIONE_AL_SERVIZIO_DI_AUTENTICAZIONE_NON_RIUSCITA);
        }
        return null;
    }
}
	