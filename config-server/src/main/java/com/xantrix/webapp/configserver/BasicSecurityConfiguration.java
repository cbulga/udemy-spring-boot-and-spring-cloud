package com.xantrix.webapp.configserver;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Slf4j
public class BasicSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${userpwd}")
    String user_password;

    @Value("${adminpwd}")
    String admin_password;

    @Value("${jasypt.encryptor.password}")
    String password;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.debug("userpwd={}", this.user_password);
        log.debug("adminpwd={}", this.admin_password);
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(new BCryptPasswordEncoder().encode(decryptString(user_password)))
                .roles("USER")
            .and()
                .withUser("admin")
                .password(new BCryptPasswordEncoder().encode(decryptString(admin_password)))
                .roles("USER", "ACTUATOR");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .httpBasic()
            .and()
                .authorizeRequests()
                .antMatchers("/actuator/**").hasAuthority("ROLE_ACTUATOR")
                .antMatchers("/**").hasAuthority("ROLE_USER");
    }

    protected String decryptString(String passwordToDecrypt) {
        StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
        decryptor.setPassword(this.password);
        return decryptor.decrypt(passwordToDecrypt);
    }

    protected String encrypt(String stringToEncrypt) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(this.password);
        return encryptor.encrypt(stringToEncrypt);
    }
}
