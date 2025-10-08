package com.smilesmile1973.clientoauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticlesController {

    private static final Logger LOG = LoggerFactory.getLogger(ArticlesController.class);

    @GetMapping(value = "/articles")
    public String[] getArticles(
            @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") OAuth2AuthorizedClient authorizedClient) {
        LOG.info("Accessing /articles endpoint with authorized client: {}",
                authorizedClient != null ? "present (token injected)" : "missing");
        if (authorizedClient != null) {
            LOG.info("Access token details: scopes={}, expiresAt={}", authorizedClient.getAccessToken().getScopes(),
                    authorizedClient.getAccessToken().getExpiresAt());
        }
        String[] results = { "pull", "shoes", "socks" };
        return results;
    }
}
