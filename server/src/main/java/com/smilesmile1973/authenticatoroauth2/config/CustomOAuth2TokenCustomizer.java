package com.smilesmile1973.authenticatoroauth2.config;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

/**
 * Custom OAuth2 token customizer that synchronizes ID token expiration with access token TTL.
 *
 * <p>This component implements {@link OAuth2TokenCustomizer} to modify JWT encoding context
 * specifically for OIDC ID tokens. It ensures that ID tokens have the same time-to-live (TTL)
 * as access tokens by reading the access token TTL from the registered client's token settings
 * and applying it to the ID token claims.</p>
 *
 * <p>The customizer only processes ID tokens and leaves other token types unchanged.
 * This helps maintain consistent token lifetimes across the OAuth2/OIDC flow.</p>
 *
 * @author smilesmile1973
 * @see OAuth2TokenCustomizer
 * @see JwtEncodingContext
 * @see OidcIdToken
 * @since 1.0
 */
@Component
public class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2TokenCustomizer.class);

    /**
     * Customizes the JWT encoding context for ID tokens by synchronizing their expiration
     * time with the access token TTL.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Checks if the current token being processed is an ID token</li>
     *   <li>Retrieves the access token TTL from the registered client's token settings</li>
     *   <li>Sets the ID token's issued-at time to the current instant</li>
     *   <li>Sets the ID token's expiration time based on the access token TTL</li>
     *   <li>Logs the customization details for monitoring purposes</li>
     * </ul>
     *
     * <p>If the token being processed is not an ID token, this method performs no operations
     * and returns immediately.</p>
     *
     * @param context the JWT encoding context containing token information, registered client
     *                details, and claims builder. Must not be {@code null}.
     * @throws NullPointerException  if context is null
     * @throws IllegalStateException if the registered client or its token settings are not properly configured
     * @see JwtEncodingContext#getTokenType()
     * @see JwtEncodingContext#getRegisteredClient()
     * @see JwtEncodingContext#getClaims()
     */
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