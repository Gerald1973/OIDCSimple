package com.smilesmile1973.authenticatoroauth2.service;

import com.smilesmile1973.authenticatoroauth2.model.UserSessionDTO;
import com.smilesmile1973.authenticatoroauth2.model.UserXml;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom implementation of {@link OAuth2AuthorizationService} that extends the functionality
 * of {@link InMemoryOAuth2AuthorizationService} by providing additional tracking capabilities
 * for OAuth2 authorizations by principal name.
 *
 * <p>This service maintains an in-memory store of OAuth2 authorizations and provides
 * enhanced logging and the ability to retrieve all authorizations for a specific principal.
 * It uses a concurrent hash map to ensure thread-safety when accessing authorizations
 * by principal name.</p>
 *
 * <p>The service delegates core authorization operations to Spring Security's
 * {@link InMemoryOAuth2AuthorizationService} while maintaining additional indexing
 * for efficient principal-based lookups.</p>
 *
 * @author smilesmile1973
 * @since 1.0
 */
@Component
public class CustomOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2AuthorizationService.class);
    /**
     * Delegate service that handles the core OAuth2 authorization operations.
     */
    private InMemoryOAuth2AuthorizationService delegate;
    /**
     * Thread-safe map that maintains a collection of authorizations indexed by principal name.
     * This allows for efficient retrieval of all authorizations belonging to a specific user.
     */
    private final Map<String, List<OAuth2Authorization>> principalAuthorizations = new ConcurrentHashMap<>();
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Initializes the delegate OAuth2 authorization service after bean construction.
     * This method is automatically called by Spring after dependency injection is complete.
     */
    @PostConstruct
    private void init() {
        this.delegate = new InMemoryOAuth2AuthorizationService();
    }

    /**
     * Saves an OAuth2 authorization to the store and indexes it by principal name.
     *
     * <p>This method delegates the save operation to the underlying service and
     * additionally maintains a mapping of authorizations by principal name for
     * efficient lookups. Logging is performed to track authorization creation
     * and refresh token generation.</p>
     *
     * @param authorization the OAuth2 authorization to save, must not be null
     * @throws IllegalArgumentException if authorization is null
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        delegate.save(authorization);
        principalAuthorizations.computeIfAbsent(authorization.getPrincipalName(), k -> new ArrayList<>());
        if (!principalAuthorizations.get(authorization.getPrincipalName()).contains(authorization)) {
            principalAuthorizations.get(authorization.getPrincipalName()).add(authorization);
        }
        LOG.info("Saved authorization for principal: {}", authorization.getPrincipalName());
        if (authorization.getRefreshToken() != null) {
            LOG.info("Refresh token generated/updated for principal: {} (expires at: {})",
                    authorization.getPrincipalName(),
                    authorization.getRefreshToken().getToken().getExpiresAt());
        }
    }

    /**
     * Removes an OAuth2 authorization from the store and cleans up the principal index.
     *
     * <p>This method delegates the removal operation to the underlying service and
     * removes the authorization from the principal-based index. If no more authorizations
     * exist for the principal, the principal entry is completely removed from the index.
     * Logging is performed to track authorization removal and refresh token revocation.</p>
     *
     * @param authorization the OAuth2 authorization to remove, must not be null
     * @throws IllegalArgumentException if authorization is null
     */
    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        delegate.remove(authorization);
        this.principalAuthorizations.remove(authorization.getId(), authorization);
        LOG.debug("Removed authorization for principal: {}", authorization.getPrincipalName());
        if (authorization.getRefreshToken() != null) {
            LOG.info("Refresh token revoked for principal: {}", authorization.getPrincipalName());
        }
    }

    /**
     * Finds an OAuth2 authorization by its unique identifier.
     *
     * @param id the unique identifier of the authorization
     * @return the OAuth2 authorization if found, null otherwise
     */
    @Override
    public OAuth2Authorization findById(String id) {
        return delegate.findById(id);
    }

    /**
     * Finds an OAuth2 authorization by token value and token type.
     *
     * @param token     the token value to search for
     * @param tokenType the type of token (access token, refresh token, etc.)
     * @return the OAuth2 authorization containing the specified token if found, null otherwise
     */
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return delegate.findByToken(token, tokenType);
    }

    public List<OAuth2Authorization> findByPrincipalName(String principalName) {
        return principalAuthorizations.get(principalName);
    }

    public List<UserSessionDTO> getUsersSession() {
        LOG.info("Listing users with active sessions");
        List<UserSessionDTO> usersSessions = new ArrayList<>();
        List<UserXml> connectedUsers = this.customUserDetailsService.getUsers();
        for (UserXml connectedUser : connectedUsers) {
            UserSessionDTO userSessionDTO = getUserSession(connectedUser.getUsername());
            if (!CollectionUtils.isEmpty(userSessionDTO.getoAuth2Authorizations())) {
                usersSessions.add(userSessionDTO);
            }
        }
        return usersSessions;
    }

    public UserSessionDTO getUserSession(String userName) {
        UserXml userXML = this.customUserDetailsService.getUserByUsername(userName);
        List<OAuth2Authorization> authorizations = this.findByPrincipalName(userName);
        UserSessionDTO result = new UserSessionDTO();
        result.setUserXml(userXML);
        result.getoAuth2Authorizations().clear();
        if (!CollectionUtils.isEmpty(authorizations)) {
            for (OAuth2Authorization authorization : authorizations) {
                result.getoAuth2Authorizations().add(authorization);
            }
        }
        return result;
    }
}