package com.smilesmile1973.authenticatoroauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smilesmile1973.authenticatoroauth2.service.CustomOAuth2AuthorizationService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class TokenController {

    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class); // MODIFIED: Updated to
                                                                                      // TokenController.class

    @Autowired
    private OAuth2AuthorizationService authorizationService; // Note: This will be the custom service

    @PostMapping("/revoke-token")
    @PreAuthorize("hasRole('ADMIN')") // Secure for admin role only
    public ResponseEntity<String> revokeToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            LOG.warn("Revocation request missing token");
            return ResponseEntity.badRequest().body("Token is required");
        }

        // Find authorization by access token
        OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            // Fallback to check if it's a refresh token
            authorization = authorizationService.findByToken(token, OAuth2TokenType.REFRESH_TOKEN);
        }

        if (authorization != null) {
            authorizationService.remove(authorization);
            if (!CollectionUtils.isEmpty(authorization.getAttributes())) {
                String userName = authorization.getAttribute(OAuth2TokenIntrospectionClaimNames.SUB);
                if (userName != null) {
                    LOG.info("Token revoked for user: {}", userName);
                }
            }
            return ResponseEntity.ok("Token revoked successfully");
        } else {
            LOG.warn("No authorization found for token");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found or already revoked");
        }
    }

    @GetMapping("/list-tokens/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listTokensByUsername(@PathVariable String username) {
        if (!(authorizationService instanceof CustomOAuth2AuthorizationService)) {
            LOG.error("Authorization service is not custom - cannot list by principal");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        CustomOAuth2AuthorizationService customService = (CustomOAuth2AuthorizationService) authorizationService;
        List<OAuth2Authorization> authorizations = customService.findByPrincipalName(username);

        List<Map<String, Object>> tokens = new ArrayList<>();
        for (OAuth2Authorization auth : authorizations) {
            Map<String, Object> tokenInfo = new HashMap<>();

            // Access Token info (if present)
            if (auth.getAccessToken() != null) {
                Map<String, Object> accessToken = new HashMap<>();
                accessToken.put("expiresAt", auth.getAccessToken().getToken().getExpiresAt());
                accessToken.put("scopes", auth.getAccessToken().getToken().getScopes());
                accessToken.put("tokenvalue", auth.getAccessToken().getToken().getTokenValue());
                tokenInfo.put("accessToken", accessToken);
            }

            // Refresh Token info (if present)
            if (auth.getRefreshToken() != null) {
                Map<String, Object> refreshToken = new HashMap<>();
                refreshToken.put("expiresAt", auth.getRefreshToken().getToken().getExpiresAt());
                tokenInfo.put("refreshToken", refreshToken);
            }

            // Other metadata
            tokenInfo.put("authorizationId", auth.getId());
            tokenInfo.put("registeredClientId", auth.getRegisteredClientId());
            tokenInfo.put("authorizationGrantType", auth.getAuthorizationGrantType().getValue());

            tokens.add(tokenInfo);
        }

        if (tokens.isEmpty()) {
            LOG.info("No tokens found for username: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tokens);
        }

        LOG.info("Listed {} tokens for username: {}", tokens.size(), username);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/current-user") // MODIFIED: New method added to check if connected and get username
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            LOG.info("Current user checked: authenticated=true, username={}", authentication.getName());
        } else {
            response.put("authenticated", false);
            response.put("username", null);
            LOG.info("Current user checked: authenticated=false");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            request.logout();
            LOG.info("User logged out successfully");
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            LOG.error("Logout failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }
    }
}