package com.smilesmile1973.authenticatoroauth2.service;

import com.smilesmile1973.authenticatoroauth2.model.UserXml;
import com.smilesmile1973.authenticatoroauth2.model.UsersXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RegisteredUserLoader {

    private static final Logger LOG = LoggerFactory.getLogger(RegisteredUserLoader.class);

    public Map<String, UserXml> loadUsersFromXML(String usersConfigPath) throws Exception {
        Map<String, UserXml> userDetailsMap = new TreeMap<>();
        InputStream xmlStream = null;
        File configFile = new File(usersConfigPath);
        if (configFile.exists() && configFile.isFile()) {
            xmlStream = new FileInputStream(configFile);
            LOG.info("Loading users from filesystem path: {}", usersConfigPath);
        } else {
            xmlStream = getClass().getClassLoader().getResourceAsStream(usersConfigPath);
            if (xmlStream == null) {
                throw new RuntimeException("users.xml file not found.");
            }
            LOG.info("Loading users from classpath: {}", usersConfigPath);
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(UsersXml.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        UsersXml usersXml = (UsersXml) unmarshaller.unmarshal(xmlStream);
        for (UserXml userXml : usersXml.getUsers()) {
            if (userDetailsMap.get(userXml.getUsername()) == null) {
                userDetailsMap.put(userXml.getUsername(), userXml);
                LOG.info("The following user is registered : {}", userXml.getUsername());
            } else {
                LOG.warn("The user {} is already defined. First occurrence kept.", userXml.getUsername());
            }
        }
        LOG.info("{} users loaded from users.xml", userDetailsMap.size());
        return userDetailsMap;
    }
}
