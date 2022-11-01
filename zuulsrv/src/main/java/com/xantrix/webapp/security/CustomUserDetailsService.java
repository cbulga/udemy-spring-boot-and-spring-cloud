package com.xantrix.webapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service("customUserDetailsService")
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserConfig userConfig;

    public CustomUserDetailsService(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        String errMsg;

        if (userId == null || userId.length() < 2) {
            errMsg = "Nome utente assente o non valido";
            log.warn(errMsg);
            throw new UsernameNotFoundException(errMsg);
        }

        Utenti utente = this.getHttpValue(userId);

        if (utente == null) {
            errMsg = String.format("Utente %s non Trovato!!", userId);
            log.warn(errMsg);
            throw new UsernameNotFoundException(errMsg);
        }

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(utente.getUserId());
        builder.disabled(!utente.getAttivo().equals("Si"));
        builder.password(utente.getPassword());

        String[] profili = utente.getRuoli()
                .stream().map(a -> "ROLE_" + a).toArray(String[]::new);
        builder.authorities(profili);
        return builder.build();
    }

    private Utenti getHttpValue(String userId) {
        URI url;

        try {
            String srvUrl = userConfig.getSrvUrl();
            url = new URI(srvUrl + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(userConfig.getUserId(), userConfig.getPassword()));

        try {
            return restTemplate.getForObject(url, Utenti.class);
        } catch (Exception e) {
            log.warn("Connessione al servizio di autenticazione non riuscita!!");
        }

        return null;
    }
}
	