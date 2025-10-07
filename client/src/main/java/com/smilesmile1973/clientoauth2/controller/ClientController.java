package com.smilesmile1973.clientoauth2.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            // Ajoute le nom de l'utilisateur au modèle si l'utilisateur est authentifié
            model.addAttribute("username", principal.getAttribute("sub"));
        }
        return "index";
    }
}