package com.smilesmile1973.clientoauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableWebSecurity
public class SecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LOG.info("Initializing OAuth2 Client security filter chain");
        http
          .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests.anyRequest().authenticated()
          )
          .oauth2Login(oauth2Login ->
            oauth2Login.loginPage("/oauth2/authorization/articles-client-oidc"))
          .oauth2Client(withDefaults());
        return http.build();
    }
}