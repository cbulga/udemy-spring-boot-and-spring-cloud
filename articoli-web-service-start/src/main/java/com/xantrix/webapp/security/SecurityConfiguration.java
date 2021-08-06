package com.xantrix.webapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] USER_MATCHER = {"/api/articoli/cerca/**"};
    private static final String[] ADMIN_MATCHER = {
            "/api/articoli/inserisci/**",
            "/api/articoli/modifica/**",
            "/api/articoli/elimina/**"
    };
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    private static final String REALM = "REAME";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                // OPTIONS method calls will be ignored by security (useful for Angular applications)
                .antMatchers(HttpMethod.OPTIONS, "/**")
                // Spring Security should completely ignore URLs starting with /resources/
                .antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(USER_MATCHER).hasAnyRole(USER_ROLE)
                .antMatchers(ADMIN_MATCHER).hasAnyRole(ADMIN_ROLE)
                .anyRequest().authenticated()
                .and()
                .httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private AuthEntryPoint getBasicAuthEntryPoint() {
        return new AuthEntryPoint();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                // enable in memory based authentication with a user named "user" and "admin"
                .inMemoryAuthentication()
                .withUser("user").password(new BCryptPasswordEncoder().encode("password")).roles("USER")
                .and()
                .withUser("admin").password(new BCryptPasswordEncoder().encode("password")).roles("USER", "ADMIN");
    }
}
