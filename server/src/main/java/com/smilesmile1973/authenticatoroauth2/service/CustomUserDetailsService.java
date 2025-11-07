package com.smilesmile1973.authenticatoroauth2.service;

import com.smilesmile1973.authenticatoroauth2.model.UserXml;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Custom implementation of Spring Security's UserDetailsService that loads user details
 * from an XML configuration file.
 *
 * <p>This service is responsible for authenticating users by loading their credentials
 * and roles from a configurable XML file. It implements the UserDetailsService interface
 * to integrate with Spring Security's authentication mechanism.</p>
 *
 * <p>The XML file path can be configured using the {@code users.config.path} property,
 * with a default value of "users.xml".</p>
 *
 * @author smilesmile1973
 * @since 1.0
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);
    /**
     * The path to the XML file containing user configuration.
     * Can be configured via the {@code users.config.path} property.
     * Defaults to "users.xml" if not specified.
     */
    @Value("${users.config.path:users.xml}")
    private String usersConfigPath;
    /**
     * Service responsible for loading user data from XML files.
     */
    @Autowired
    private RegisteredUserLoader registeredUserLoader;
    /**
     * In-memory cache of user details loaded from the XML configuration file.
     * Maps username to UserXml objects for quick lookup during authentication.
     */
    private Map<String, UserXml> userDetailsMap = null;

    /**
     * Default constructor for CustomUserDetailsService.
     */
    public CustomUserDetailsService() {
    }

    /**
     * Initializes the user details map by loading user data from the configured XML file.
     * This method is automatically called after dependency injection is complete.
     *
     * @throws Exception if there's an error loading or parsing the XML file
     */
    @PostConstruct
    private void buildUserDetailsMap() throws Exception {
        this.userDetailsMap = this.registeredUserLoader.loadUsersFromXML(usersConfigPath);
    }

    /**
     * Loads user details by username for Spring Security authentication.
     *
     * <p>This method searches for the specified username in the pre-loaded user details map
     * and returns a UserDetails object containing the user's credentials and roles.
     * The roles are expected to be comma-separated in the XML configuration.</p>
     *
     * @param userName the username to search for (case-sensitive)
     * @return UserDetails object containing user credentials and roles
     * @throws UsernameNotFoundException if the specified username is not found in the configuration
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(String)
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOG.info("Loading user: {}", userName);
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

    public List<UserXml> getUsers() {
        List<UserXml> results = new ArrayList<>();
        for (UserXml original : userDetailsMap.values()) {
            results.add(original.clone());
        }
        return results;
    }

    public UserXml getUserByUsername(String userName) {
        UserXml original = userDetailsMap.get(userName);
        UserXml  result = null;
        if (original != null) {
            result = original.clone();
        }
        return result;
    }
}