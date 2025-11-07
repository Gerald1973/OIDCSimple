package com.smilesmile1973.authenticatoroauth2.config;

import com.smilesmile1973.authenticatoroauth2.repository.CustomInMemoryRegisteredClientRepository;
import com.smilesmile1973.authenticatoroauth2.service.RegisteredClientLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
//@EnableWebSecurity(debug = true)
public class DefaultSecurityConfig {
    @Value("${clients.config.path:clients.xml}")
    private String clientsConfigPath;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityConfig.class);
    @Autowired
    private RegisteredClientLoader registeredClientLoader;

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        LOG.info("Initializing OAuth2 Authorization Server filter chain");
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(withDefaults());
        return http.formLogin(withDefaults()).build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        LOG.info("Initializing default security filter chain for non-OAuth2 endpoints");
        http.authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .anyRequest().authenticated())
                .formLogin(withDefaults());
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() throws Exception {
        LOG.info("Loading RegisteredClients from XML file");
        List<RegisteredClient> clients = registeredClientLoader.loadClientsFromXml(clientsConfigPath);
        if (clients.isEmpty()) {
            LOG.warn("No clients loaded from XML, repository will be empty");
        }
        return new CustomInMemoryRegisteredClientRepository(clients);
    }
}