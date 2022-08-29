package com.xantrix.webapp.security;

import lombok.SneakyThrows;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

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

    private static final String[] USER_MATCHER = {
            "/api/prezzi/**",
            "/api/listino/cerca/**",
            "/info"};
    private static final String[] ADMIN_MATCHER = {
            "/api/prezzi/elimina/**",
            "/api/listino/inserisci/**",
            "/api/listino/elimina/**"};
    private static final String[] NOAUTH_MATCHER = {
            "/v3/api-docs/**",        // swagger
            "/webjars/**",            // swagger-ui webjars
            "/swagger-resources/**",  // swagger-ui resources
            "/configuration/**",      // swagger configuration
            "/swagger-ui.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(ADMIN_MATCHER).hasAnyRole("admin", "Admin")
                .antMatchers(USER_MATCHER).hasAnyRole("user", "User")
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.GET, NOAUTH_MATCHER).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    }

//    @Override
//    public void configure(WebSecurity webSecurity) {
//        webSecurity.ignoring()
//                .antMatchers(HttpMethod.OPTIONS, "/**");
//    }

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