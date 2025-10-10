package com.smilesmile1973.authenticatoroauth2.service;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.smilesmile1973.authenticatoroauth2.util.ClientXmlPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import com.smilesmile1973.authenticatoroauth2.model.ClientXml;
import com.smilesmile1973.authenticatoroauth2.model.ClientsXml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

@Service
public class RegisteredClientLoader {

    private static final Logger LOG = LoggerFactory.getLogger(RegisteredClientLoader.class);

    /**
     * Load RegisteredClient instances from an XML file.
     *
     * @param xmlFileName the name of the XML file (e.g., "clients.xml")
     * @return a list of RegisteredClient instances
     */
    public List<RegisteredClient> loadClientsFromXml(String xmlFileName) {
        List<RegisteredClient> registeredClients = new ArrayList<>();

        try {
            InputStream xmlStream = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            if (xmlStream == null) {
                throw new RuntimeException("XML file not found: " + xmlFileName);
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(ClientsXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ClientsXml clientsXml = (ClientsXml) unmarshaller.unmarshal(xmlStream);

            for (ClientXml clientXml : clientsXml.getClients()) {
                RegisteredClient registeredClient = buildRegisteredClient(clientXml);
                registeredClients.add(registeredClient);
                LOG.info("Loaded client: {} with id: {}", clientXml.getClientId(), clientXml.getId());
                ClientXmlPrinter.printAsTable(clientXml);
            }

            LOG.info("Successfully loaded {} clients from {}", registeredClients.size(), xmlFileName);
        } catch (Exception e) {
            LOG.error("Error loading clients from XML file: {}", xmlFileName, e);
            throw new RuntimeException("Failed to load clients from XML", e);
        }

        return registeredClients;
    }

    /**
     * Build a RegisteredClient from ClientXml data.
     *
     * @param clientXml the ClientXml instance
     * @return a RegisteredClient instance
     */
    private RegisteredClient buildRegisteredClient(ClientXml clientXml) {
        // Build TokenSettings
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(
                        clientXml.getAccessTokenDuration() != null ? clientXml.getAccessTokenDuration() : 900))
                .refreshTokenTimeToLive(Duration.ofSeconds(
                        clientXml.getRefreshTokenDuration() != null ? clientXml.getRefreshTokenDuration() : 86400))
                .build();

        // Build RegisteredClient
        RegisteredClient.Builder builder = RegisteredClient.withId(clientXml.getId())
                .clientId(clientXml.getClientId())
                .clientSecret(clientXml.getClientSecret())
                .clientName(clientXml.getClientName());

        // Add authentication methods
        if (clientXml.getAuthenticationMethods() != null) {
            for (String method : clientXml.getAuthenticationMethods().split(",")) {
                builder.clientAuthenticationMethod(parseAuthenticationMethod(method.trim()));
            }
        }

        // Add grant types
        if (clientXml.getGrantTypes() != null) {
            for (String grantType : clientXml.getGrantTypes().split(",")) {
                builder.authorizationGrantType(parseGrantType(grantType.trim()));
            }
        }

        // Add redirect URIs
        if (clientXml.getRedirectUris() != null) {
            for (String redirectUri : clientXml.getRedirectUris().split(",")) {
                builder.redirectUri(redirectUri.trim());
            }
        }

        // Add scopes
        if (clientXml.getScopes() != null) {
            for (String scope : clientXml.getScopes().split(",")) {
                builder.scope(scope.trim());
            }
        }

        // Set token settings
        builder.tokenSettings(tokenSettings);

        return builder.build();
    }

    /**
     * Parse ClientAuthenticationMethod from string.
     *
     * @param method the method as string
     * @return ClientAuthenticationMethod
     */
    private ClientAuthenticationMethod parseAuthenticationMethod(String method) {
        switch (method.toLowerCase()) {
            case "client_secret_basic":
                return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            case "client_secret_post":
                return ClientAuthenticationMethod.CLIENT_SECRET_POST;
            case "client_secret_jwt":
                return ClientAuthenticationMethod.CLIENT_SECRET_JWT;
            case "private_key_jwt":
                return ClientAuthenticationMethod.PRIVATE_KEY_JWT;
            case "none":
                return ClientAuthenticationMethod.NONE;
            default:
                LOG.warn("Unknown authentication method: {}, defaulting to client_secret_basic", method);
                return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }
    }

    /**
     * Parse AuthorizationGrantType from string.
     *
     * @param grantType the grant type as string
     * @return AuthorizationGrantType
     */
    private AuthorizationGrantType parseGrantType(String grantType) {
        switch (grantType.toLowerCase()) {
            case "authorization_code":
                return AuthorizationGrantType.AUTHORIZATION_CODE;
            case "refresh_token":
                return AuthorizationGrantType.REFRESH_TOKEN;
            case "client_credentials":
                return AuthorizationGrantType.CLIENT_CREDENTIALS;
            case "password":
                LOG.warn("Password grant type is deprecated and not recommended");
                return new AuthorizationGrantType("password");
            default:
                LOG.warn("Unknown grant type: {}, using as custom grant type", grantType);
                return new AuthorizationGrantType(grantType);
        }
    }
}
