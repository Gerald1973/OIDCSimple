package com.smilesmile1973.authenticatoroauth2.config;

import com.smilesmile1973.authenticatoroauth2.model.UserXml;
import com.smilesmile1973.authenticatoroauth2.model.UsersXml;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Value("${users.config.path:users.xml}")
    private String usersConfigPath;

    private final Map<String, UserXml> userDetailsMap = new TreeMap<>();

    public CustomUserDetailsService() {
    }

    @PostConstruct
    private void buildUserDetailsMap() {
        try {
            InputStream xmlStream = getClass().getClassLoader().getResourceAsStream(usersConfigPath);
            if (xmlStream == null) {
                throw new RuntimeException("users.xml file not found.");
            }
            LOG.info(xmlStream.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(UsersXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            UsersXml usersXml = (UsersXml) unmarshaller.unmarshal(xmlStream);
            for (UserXml userXml : usersXml.getUsers()) {
                if (this.userDetailsMap.get(userXml.getUsername()) == null) {
                    this.userDetailsMap.put(userXml.getUsername(), userXml);
                    LOG.info("The following user is registered : {}", userXml.getUsername());
                } else {
                    LOG.warn("The user {} is already defined. First occurrence kept.", userXml.getUsername());
                }
            }
            LOG.info("{} users loaded from users.xml", this.userDetailsMap.size());
        } catch (Exception e) {
            LOG.error("Users fetching failed.", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserXml userXml = this.userDetailsMap.get(username);
        UserDetails result = null;
        if (userXml != null) {
            result = User.builder()
                    .username(userXml.getUsername())
                    .password(userXml.getPassword())
                    .roles(userXml.getRoles().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException("The user " + username + " is not found.");
        }
        return result;
    }
}
