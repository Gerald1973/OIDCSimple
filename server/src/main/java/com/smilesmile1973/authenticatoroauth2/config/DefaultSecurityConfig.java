package com.smilesmile1973.authenticatoroauth2.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.smilesmile1973.authenticatoroauth2.service.CustomOAuth2AuthorizationService;

@Configuration
@EnableWebSecurity
public class DefaultSecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityConfig.class);

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        LOG.info("Initializing OAuth2 Authorization Server filter chain");
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(withDefaults()); // Enable OpenID Connect 1.0
        return http.formLogin(withDefaults()).build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        LOG.info("Initializing default security filter chain for non-OAuth2 endpoints");
        http.authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.requestMatchers("/admin/**").hasRole("ADMIN").anyRequest()
                        .authenticated())
                .formLogin(withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.builder()
                .username("admin")
                .password("password")
                .passwordEncoder(encoder::encode)
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new CustomOAuth2AuthorizationService(); // MODIFIED: Instantiates the new custom service
    }

}