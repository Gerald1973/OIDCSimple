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

/**
 * REST controller for managing OAuth2 tokens and user authentication.
 * Provides administrative endpoints for token management, user information,
 * and authentication operations.
 *
 * <p>All administrative operations require ADMIN role authorization.</p>
 *
 * @author smilesmile1973
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/admin")
public class TokenController {
    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);
    @Autowired
    private OAuth2AuthorizationService customOAuth2AuthorizationService;

    /**
     * Revokes an OAuth2 token (access or refresh token).
     *
     * <p>This endpoint allows administrators to revoke tokens by providing
     * the token value in the request body. The method will search for both
     * access and refresh tokens and remove the associated authorization.</p>
     *
     * @param request a map containing the token to be revoked with key "token"
     * @return ResponseEntity with success message if token is revoked,
     * bad request if token is missing, or not found if token doesn't exist
     * @throws IllegalArgumentException if request body is malformed
     * @see OAuth2Authorization
     * @see OAuth2TokenType
     */
    @PostMapping("/revoke-token")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> revokeToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            LOG.warn("Revocation request missing token");
            return ResponseEntity.badRequest().body("Token is required");
        }
        OAuth2Authorization authorization = customOAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (authorization == null) {
            authorization = customOAuth2AuthorizationService.findByToken(token, OAuth2TokenType.REFRESH_TOKEN);
        }
        if (authorization != null) {
            customOAuth2AuthorizationService.remove(authorization);
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

    /**
     * Retrieves information about the currently authenticated user.
     *
     * <p>This endpoint returns the authentication status and username
     * of the current user based on the security context. No special
     * authorization is required for this endpoint.</p>
     *
     * @return ResponseEntity containing a map with authentication status
     * and username information
     * @see SecurityContextHolder
     * @see Authentication
     */
    @GetMapping("/current-user")
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

    /**
     * Logs out the current user by invalidating their session.
     *
     * <p>This endpoint performs a logout operation by calling the
     * HttpServletRequest.logout() method, which invalidates the
     * current user session and clears authentication information.</p>
     *
     * @param request the HTTP servlet request containing session information
     * @return ResponseEntity with success message if logout is successful,
     * or internal server error if logout fails
     * @throws Exception if logout operation fails
     * @see HttpServletRequest#logout()
     */
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