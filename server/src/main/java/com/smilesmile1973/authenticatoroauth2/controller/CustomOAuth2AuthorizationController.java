package com.smilesmile1973.authenticatoroauth2.controller;

import com.smilesmile1973.authenticatoroauth2.model.UserSessionDTO;
import com.smilesmile1973.authenticatoroauth2.model.UserXml;
import com.smilesmile1973.authenticatoroauth2.service.CustomOAuth2AuthorizationService;
import com.smilesmile1973.authenticatoroauth2.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class CustomOAuth2AuthorizationController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2AuthorizationController.class);
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomOAuth2AuthorizationService customOAuth2AuthorizationService;

    @GetMapping("/list")
    public ResponseEntity<List<UserXml>> getUsers() {
        List<UserXml> results = this.customUserDetailsService.getUsers();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/users-session")
    public ResponseEntity<List<UserSessionDTO>> listUsersWithActiveSessions() {
        List<UserSessionDTO> results = this.customOAuth2AuthorizationService.getUsersSession();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user-session/{userName}")
    public ResponseEntity<UserSessionDTO> getActiveSession(@PathVariable String userName) {
        UserSessionDTO result = this.customOAuth2AuthorizationService.getUserSession(userName);
        return ResponseEntity.ok(result);
    }
}
