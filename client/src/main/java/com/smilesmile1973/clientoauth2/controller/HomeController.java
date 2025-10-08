package com.smilesmile1973.clientoauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {

    @GetMapping("/")
    public RedirectView redirectToPrincipalInfo() {
        return new RedirectView("/principal/info");
    }
}
