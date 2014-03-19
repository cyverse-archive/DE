package org.iplantc.de.server;

import java.util.Properties;


/**
 * Contains methods for obtaining information from confluence client configuration settings.
 *
 * @author hariolf
 *
 */
@SuppressWarnings("nls")
public class ConfluenceProperties {

    // The prefix for all of the properties.
    private static final String PREFIX = "org.iplantc.discoveryenvironment.confluence";

    private static final String CONFLUENCE_BASE_URL = PREFIX + ".baseUrl";
    private static final String CONFLUENCE_PARENT_PAGE = PREFIX + ".parentPageName";
    private static final String CONFLUENCE_USER = PREFIX + ".user";
    private static final String CONFLUENCE_PASSWORD = PREFIX + ".password";
    private static final String CONFLUENCE_SPACE_NAME = PREFIX + ".spaceName";
    private static final String CONFLUENCE_SPACE_URL = PREFIX + ".spaceUrl";
    /** this is an internationalized string #translate #i18n */
    private static final String CONFLUENCE_COMMENT_SUFFIX = PREFIX + ".ratingCommentSuffix";
    private Properties properties;

    /**
     * The list of required properties.
     */
    private final static String[] REQUIRED_PROPERTIES = {CONFLUENCE_BASE_URL, CONFLUENCE_PARENT_PAGE,
            CONFLUENCE_USER, CONFLUENCE_PASSWORD, CONFLUENCE_SPACE_NAME, CONFLUENCE_SPACE_URL,
            CONFLUENCE_COMMENT_SUFFIX};

    /**
     * Creates a new ConfluenceProperties object and loads properties.
     *
     * @param properties = the configuration properties.
     */
    public ConfluenceProperties(Properties properties) {
        this.properties = properties;
        validateProperties(REQUIRED_PROPERTIES);
    }

    /**
     * Validates that we have values for all required properties.
     *
     * @throws RuntimeException if a required property isn't found in the file
     */
    private void validateProperties(String[] propertyNames) {
        for (String propertyName : propertyNames) {
            String propertyValue = properties.getProperty(propertyName);
            if (propertyValue == null || propertyValue.equals("")) {
                throw new RuntimeException("missing required property: " + propertyName);
            }
        }
    }

    /**
     * Gets the base URL used to access the Confluence wiki.
     *
     * @return the URL as a string.
     */
    public String getConfluenceBaseUrl() {
        return properties.getProperty(CONFLUENCE_BASE_URL);
    }

    /**
     * Gets the name of the 'List of Applications' page.
     *
     * @return the name as a string.
     */
    public String getConfluenceParentPage() {
        return properties.getProperty(CONFLUENCE_PARENT_PAGE);
    }

    /**
     * Gets the Confluence user for adding documentation pages.
     *
     * @return the user name
     */
    public String getConfluenceUser() {
        return properties.getProperty(CONFLUENCE_USER);
    }

    /**
     * Gets the Confluence password for adding documentation pages.
     *
     * @return the password
     */
    public String getConfluencePassword() {
        return properties.getProperty(CONFLUENCE_PASSWORD);
    }

    /**
     * Gets the name of the 'DE Applications' space in Confluence.
     *
     * @return the name as a string.
     */
    public String getConfluenceSpaceName() {
        return properties.getProperty(CONFLUENCE_SPACE_NAME);
    }

    /**
     * Gets the URL of the 'DE Applications' space in Confluence.
     *
     * @return the URL as a string.
     */
    public String getConfluenceSpaceUrl() {
        return properties.getProperty(CONFLUENCE_SPACE_URL);
    }

    /**
     * Gets localized text that is added to rating comments for the wiki; contains a placeholder for the
     * current DE user.
     *
     * @return a string representing the localized text.
     */
    public String getRatingCommentSuffix() {
        return properties.getProperty(CONFLUENCE_COMMENT_SUFFIX);
    }
}
