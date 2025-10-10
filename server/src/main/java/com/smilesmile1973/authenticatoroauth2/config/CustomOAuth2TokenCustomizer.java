package com.smilesmile1973.authenticatoroauth2.config;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2TokenCustomizer.class);

    @Override
    public void customize(JwtEncodingContext context) {
        // Check if this is an ID Token
        if (OidcIdToken.class.isAssignableFrom(context.getTokenType().getValue().getClass()) ||
                context.getTokenType().getValue().equals("id_token")) {

            // Get the access token TTL from the RegisteredClient's TokenSettings
            Duration accessTokenTTL = context.getRegisteredClient()
                    .getTokenSettings()
                    .getAccessTokenTimeToLive();

            // Set the ID Token to have the same TTL as the Access Token
            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plus(accessTokenTTL);

            context.getClaims()
                    .issuedAt(issuedAt)
                    .expiresAt(expiresAt);

            LOG.info("ID Token customized for client: {} with TTL: {} seconds",
                    context.getRegisteredClient().getClientId(),
                    accessTokenTTL.getSeconds());
        }
    }
}
