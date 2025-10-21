package com.smilesmile1973.clientoauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PrincipalController {

    private static final Logger LOG = LoggerFactory.getLogger(PrincipalController.class);

    @GetMapping("/principal/info")
    public OAuth2User getOidcUserPrincipal(@AuthenticationPrincipal final OAuth2User principal) {
        return principal;
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