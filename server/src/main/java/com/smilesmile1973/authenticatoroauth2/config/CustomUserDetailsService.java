package com.smilesmile1973.authenticatoroauth2.config;

import com.smilesmile1973.authenticatoroauth2.model.UserXml;
import com.smilesmile1973.authenticatoroauth2.service.RegisteredUserLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Value("${users.config.path:users.xml}")
    private String usersConfigPath;

    @Autowired
    private RegisteredUserLoader registeredUserLoader;

    private Map<String, UserXml> userDetailsMap = null;

    public CustomUserDetailsService() {
    }

    @PostConstruct
    private void buildUserDetailsMap() throws Exception {
       this.userDetailsMap = this.registeredUserLoader.loadUsersFromXML(usersConfigPath);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOG.info("Logg user : {}",userName);
        UserXml userXml = this.userDetailsMap.get(userName);
        UserDetails result = null;
        if (userXml != null) {
            result = User.builder()
                    .username(userXml.getUsername())
                    .password(userXml.getPassword())
                    .roles(userXml.getRoles().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException("The user " + userName + " is not found.");
        }
        return result;
    }
}
