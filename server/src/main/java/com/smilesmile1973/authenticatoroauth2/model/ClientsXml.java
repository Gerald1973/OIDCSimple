package com.smilesmile1973.authenticatoroauth2.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clients")
public class ClientsXml {

    private List<ClientXml> clients;

    @XmlElement(name = "client")
    public List<ClientXml> getClients() {
        return clients;
    }

    public void setClients(List<ClientXml> clients) {
        this.clients = clients;
    }
}
