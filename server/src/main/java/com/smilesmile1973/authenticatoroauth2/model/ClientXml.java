package com.smilesmile1973.authenticatoroauth2.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "client")
public class ClientXml {

    private String id;
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String authenticationMethods;
    private String grantTypes;
    private String redirectUris;
    private String scopes;
    private Integer accessTokenDuration;
    private Integer refreshTokenDuration;

    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "clientId")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @XmlElement(name = "clientSecret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @XmlElement(name = "clientName")
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @XmlElement(name = "authenticationMethods")
    public String getAuthenticationMethods() {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(String authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    @XmlElement(name = "grantTypes")
    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    @XmlElement(name = "redirectUris")
    public String getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(String redirectUris) {
        this.redirectUris = redirectUris;
    }

    @XmlElement(name = "scopes")
    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    @XmlElement(name = "accessTokenDuration")
    public Integer getAccessTokenDuration() {
        return accessTokenDuration;
    }

    public void setAccessTokenDuration(Integer accessTokenDuration) {
        this.accessTokenDuration = accessTokenDuration;
    }

    @XmlElement(name = "refreshTokenDuration")
    public Integer getRefreshTokenDuration() {
        return refreshTokenDuration;
    }

    public void setRefreshTokenDuration(Integer refreshTokenDuration) {
        this.refreshTokenDuration = refreshTokenDuration;
    }
}