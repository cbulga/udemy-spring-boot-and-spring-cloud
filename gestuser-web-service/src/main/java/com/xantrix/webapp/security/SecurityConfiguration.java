package com.xantrix.webapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String REALM = "REAME";
    private static final String[] USER_MATCHER = {"/api/utenti/cerca/**"};
    private static final String[] ADMIN_MATCHER = {"/api/utenti/inserisci/**", "/api/utenti/elimina/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(USER_MATCHER).hasAnyRole("USER")
                .antMatchers(ADMIN_MATCHER).hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthEntryPoint getBasicAuthEntryPoint() {
        return new AuthEntryPoint();
    }

    /* To allow Pre-flight [OPTIONS] request from browser */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserBuilder users = User.builder();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(users
                .username("Cristian")
                .password(new BCryptPasswordEncoder().encode("123Stella"))
                .roles("USER").build());

        manager.createUser(users
                .username("Admin")
                .password(new BCryptPasswordEncoder().encode("VerySecretPwd"))
                .roles("USER", "ADMIN").build());

        return manager;
    }
}
