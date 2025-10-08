package com.smilesmile1973.authenticatoroauth2.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
public class UsersXml {

    private List<UserXml> users;

    @XmlElement(name = "user")
    public List<UserXml> getUsers() {
        return users;
    }

    public void setUsers(List<UserXml> users) {
        this.users = users;
    }
}