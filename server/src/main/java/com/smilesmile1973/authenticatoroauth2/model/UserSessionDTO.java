package com.smilesmile1973.authenticatoroauth2.model;

import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.ArrayList;
import java.util.List;

public class UserSessionDTO {
    private final List<OAuth2Authorization> oAuth2Authorizations = new ArrayList<>();
    private UserXml userXml;

    public UserSessionDTO() {
    }

    public UserXml getUserXml() {
        return userXml;
    }

    public void setUserXml(UserXml userXml) {
        this.userXml = userXml;
    }

    public List<OAuth2Authorization> getoAuth2Authorizations() {
        return oAuth2Authorizations;
    }
}
