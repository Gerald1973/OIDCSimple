package com.smilesmile1973.authenticatoroauth2.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import static com.smilesmile1973.authenticatoroauth2.util.Constants.STARWORD;

@XmlRootElement(name = "user")
public class UserXml implements Cloneable {

    private String username;
    private String password;
    private String roles;

    @XmlAttribute(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlAttribute(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlAttribute(name = "roles")
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public UserXml clone() {
        try {
            UserXml clone = (UserXml) super.clone();
            clone.username = this.username;
            clone.password = STARWORD;
            clone.roles = this.roles;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported", e);
        }
    }
}