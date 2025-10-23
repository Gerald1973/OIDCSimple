package com.smilesmile1973.authenticatoroauth2.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CustomOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2AuthorizationService.class);

    private InMemoryOAuth2AuthorizationService delegate;
    private final Map<String, List<OAuth2Authorization>> principalAuthorizations = new ConcurrentHashMap<>();

   @PostConstruct
   private void init() {
       this.delegate = new InMemoryOAuth2AuthorizationService();
   }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        delegate.save(authorization);
        principalAuthorizations.computeIfAbsent(authorization.getPrincipalName(), k -> new ArrayList<>())
                .add(authorization);
        LOG.info("Saved authorization for principal: {}", authorization.getPrincipalName());
        if (authorization.getRefreshToken() != null) {
            LOG.info("Refresh token generated/updated for principal: {} (expires at: {})",
                    authorization.getPrincipalName(),
                    authorization.getRefreshToken().getToken().getExpiresAt());
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        delegate.remove(authorization);
        List<OAuth2Authorization> auths = principalAuthorizations.get(authorization.getPrincipalName());
        if (auths != null) {
            auths.remove(authorization);
            if (auths.isEmpty()) {
                principalAuthorizations.remove(authorization.getPrincipalName());
            }
        }
        LOG.debug("Removed authorization for principal: {}", authorization.getPrincipalName());
        if (authorization.getRefreshToken() != null) {
            LOG.info("Refresh token revoked for principal: {}", authorization.getPrincipalName());
        }
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return delegate.findByToken(token, tokenType);
    }

    public List<OAuth2Authorization> findByPrincipalName(String principalName) {
        return new ArrayList<>(principalAuthorizations.getOrDefault(principalName, Collections.emptyList()));
    }
}