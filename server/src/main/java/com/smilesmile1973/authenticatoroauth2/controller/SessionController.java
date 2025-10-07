package com.smilesmile1973.authenticatoroauth2.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class SessionController {

    private final OAuth2AuthorizationService authorizationService;

    public SessionController(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @DeleteMapping("/revoke/{tokenId}")
    public String revokeSession(@PathVariable String tokenId) {
        // Find the authorization by token (specify type for precision, or use null for general search)
        OAuth2Authorization authorization = authorizationService.findByToken(tokenId, new OAuth2TokenType("access_token"));
        
        if (authorization != null) {
            // Revoke the authorization if found
            authorizationService.remove(authorization);
            return "Session revoked for token: " + tokenId;
        } else {
            return "No session found for token: " + tokenId;
        }
    }
}