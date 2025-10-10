package com.smilesmile1973.clientoauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PrincipalController {

    private static final Logger LOG = LoggerFactory.getLogger(PrincipalController.class);


    @GetMapping({ "/principal/info" })
    public Map<String, Object> index(@AuthenticationPrincipal OAuth2User principal, HttpServletRequest request) {
        LOG.info("Accessing index page ({}); Principal present: {}", request.getRequestURI(), principal != null);
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.putAll(principal.getAttributes());
            LOG.info("Authenticated user sub: {}", principal.getAttribute("sub").toString());
        } else {
            response.put("error", "No authenticated principal found");
            LOG.warn("No principal present for request: {}", request.getRequestURI());
        }
        return response;
    }

    @GetMapping("/principal/logout")
    public Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            request.logout();  // Invalidate the current session and logout the user
            response.put("message", "User logged out successfully");
            LOG.info("User logged out successfully");
        } catch (Exception e) {
            response.put("error", "Logout failed: " + e.getMessage());
            LOG.error("Logout failed", e);
        }
        return response;
    }
}