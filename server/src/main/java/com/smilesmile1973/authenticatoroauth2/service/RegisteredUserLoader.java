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
import java.util.Map;
import java.util.TreeMap;

/**
 * Service class responsible for loading and managing registered users from XML configuration files.
 *
 * <p>This service supports loading user configurations from both filesystem paths and classpath resources.
 * It uses JAXB for XML unmarshalling and ensures duplicate usernames are handled gracefully by keeping
 * the first occurrence and logging warnings for duplicates.</p>
 *
 * <p>The loaded users are returned as a sorted map (TreeMap) with usernames as keys for efficient lookup.</p>
 *
 * @author smilesmile1973
 * @since 1.0
 */
@Service
public class RegisteredUserLoader {
    private static final Logger LOG = LoggerFactory.getLogger(RegisteredUserLoader.class);

    /**
     * Loads user configurations from an XML file and returns them as a map.
     *
     * <p>This method first attempts to load the XML file from the filesystem using the provided path.
     * If the file doesn't exist on the filesystem, it falls back to loading from the classpath.
     * The XML content is unmarshalled using JAXB into UserXml objects.</p>
     *
     * <p>Duplicate usernames are handled by keeping the first occurrence and logging a warning
     * for subsequent duplicates. The returned map is sorted alphabetically by username.</p>
     *
     * @param usersConfigPath the path to the users XML configuration file. Can be either a filesystem
     *                        path or a classpath resource path
     * @return a TreeMap containing username as key and UserXml object as value, sorted alphabetically
     * by username. Never returns null, but may return an empty map if no users are found
     * @throws Exception        if the XML file cannot be found, cannot be read, or contains invalid XML structure
     * @throws RuntimeException if the users.xml file is not found in either filesystem or classpath
     * @see UserXml
     * @see UsersXml
     */
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