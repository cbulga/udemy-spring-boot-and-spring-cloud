package com.xantrix.webapp.security;

import lombok.SneakyThrows;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@KeycloakConfiguration
class KeycloakWebSecurityConfigurer extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    @SneakyThrows
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    private static final String[] USER_MATCHER = {"/api/articoli/cerca/**"};
    private static final String[] ADMIN_MATCHER = {"/api/articoli/inserisci/**",
            "/api/articoli/modifica/**", "/api/articoli/elimina/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(USER_MATCHER).hasAnyRole("user", "User")
                .antMatchers(ADMIN_MATCHER).hasAnyRole("admin", "Admin")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    // for debug purposes only!
    // when keycloak returns a 401 response:
    // - decomment the code here
    // - in the controller define a private final AccessToken accessToken;
    // - define these security configurations in the com.xantrix.webapp.security.KeycloakWebSecurityConfigurer.configure(org.springframework.security.config.annotation.web.builders.HttpSecurity) method:
    //         http.csrf().disable()
    //                .authorizeRequests()
    //                .anyRequest().permitAll().and()
    ////                .antMatchers(USER_MATCHER).hasAnyRole("user", "User")
    ////                .antMatchers(ADMIN_MATCHER).hasAnyRole("admin", "Admin")
    ////                .anyRequest().authenticated()
    ////                .and()
    //                .exceptionHandling()
    //                .accessDeniedHandler(accessDeniedHandler());
    // - when in debug in the controller method you are calling, define an evaluation expression like this one to see returned roles:
    //     accessToken.getRealmAccess().getRoles()
//    @Bean
//    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
//    public AccessToken accessToken() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        return ((KeycloakSecurityContext) ((KeycloakAuthenticationToken) request.getUserPrincipal()).getCredentials()).getToken();
//    }
}