package org.iplantc.de.admin.desktop.client.models;

import org.iplantc.de.commons.client.ErrorHandler;

import java.util.Map;

public class BelphegorAdminProperties {

    private static BelphegorAdminProperties instance;

    /**
     * The prefix used in each of the property names.
     */
    private static final String PROPERTY_NAME_PREFIX = "org.iplantc.admin.";

    /**
     * Properties key of the default Beta Category ID.
     */
    private static final String CATEGORY_DEFAULT_BETA_GROUP_ID = PROPERTY_NAME_PREFIX
            + "category.defaultBetaAppCategoryId";

    /**
     * Properties key of the default Beta Category ID.
     */
    private static final String CATEGORY_DEFAULT_TRASH_GROUP_ID = PROPERTY_NAME_PREFIX
            + "category.defaultTrashAppCategoryId";

    /**
     * The name of property containing the CAS session keepalive target URL.
     */
    private static final String KEEPALIVE_TARGET = PROPERTY_NAME_PREFIX + "keepalive.target";

    /**
     * The name of the property containing the CAS session keepalive interval.
     */
    private static final String KEEPALIVE_INTERVAL = PROPERTY_NAME_PREFIX + "keepalive.interval";

    private static final String APP_DOC_URL = PROPERTY_NAME_PREFIX + "validAppWikiUrlPath";

    /**
     * Properties key of the context click enabled option
     */
    private static final String CONTEXT_CLICK_ENABLED = PROPERTY_NAME_PREFIX + "contextMenu.enabled";

    private String defaultBetaAppCategoryId;

    private String defaultTrashAppCategoryId;

    private boolean contextClickEnabled;

    private String keepaliveTarget;

    private int keepaliveInterval;

    private String[] validAppWikiUrlPath;

    public static BelphegorAdminProperties getInstance() {

        if (instance == null) {
            instance = new BelphegorAdminProperties();
        }
        return instance;
    }

    /**
     * Initializes this class from the given set of properties.
     * 
     * @param properties the properties that were fetched from the server.
     */
    public void initialize(Map<String, String> properties) {

        this.defaultTrashAppCategoryId = properties.get(CATEGORY_DEFAULT_TRASH_GROUP_ID);

        contextClickEnabled = getBooleanProperty(properties, CONTEXT_CLICK_ENABLED, false);
        keepaliveInterval = getIntProperty(properties, KEEPALIVE_INTERVAL, -1);

        defaultBetaAppCategoryId = getStringProperty(properties, CATEGORY_DEFAULT_BETA_GROUP_ID, "");
        keepaliveTarget = getStringProperty(properties, KEEPALIVE_TARGET, "");
        validAppWikiUrlPath = getStringList(properties, APP_DOC_URL);
    }

    /**
     * Alert when accessing a property fails.
     * 
     * @param e the Exception thrown.
     * @param propName the name of the property that was being accessed.
     */
    private void outputAccessFailure(Exception e, String propName) {
        ErrorHandler.post("An attempt to get property failed: " + propName, e);
    }

    /**
     * Gets a list of String values for a property.
     * 
     * The property encodes a comma-separated list of String value.
     * 
     * @param props the properties map.
     * @param propName the name of the property to extract.
     * @return the list of String values as an array.
     */
    private String[] getStringList(Map<String, String> props, String propName) {
        String[] list;
        String value = props.get(propName);
        if (value == null || value.length() == 0) {
            ErrorHandler.post("An attempt to get property failed: " + propName);
            value = "";
        }
        list = value.split(",");
        return list;
    }

    /**
     * Gets a String property value.
     * 
     * @param props the properties map.
     * @param propName the name of the property to extract.
     * @param defaultValue the default value to use.
     * @return the property value or the default value if the property value can't be obtained.
     */
    private String getStringProperty(Map<String, String> props, String propName, String defaultValue) {
        String value = props.get(propName);
        if (value == null || value.length() == 0) {
            ErrorHandler.post("An attempt to get property failed: " + propName);
            value = defaultValue;
        }
        return value;
    }

    /**
     * Gets a Boolean property value.
     * 
     * @param props the properties map.
     * @param propName the name of the property to extract.
     * @param defaultValue the default value to use.
     * @return the property value or the default value if the property value can't be obtained.
     */
    private boolean getBooleanProperty(Map<String, String> props, String propName, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(props.get(propName));
        } catch (Exception e) {
            outputAccessFailure(e, propName);
            return defaultValue;
        }
    }

    /**
     * Gets an integer property value.
     * 
     * @param props the properties map.
     * @param propName the name of the property to extract.
     * @param defaultValue the default value to use.
     * @return the property value or the default value if the property value can't be obtained.
     */
    private int getIntProperty(Map<String, String> props, String propName, int defaultValue) {
        try {
            return Integer.parseInt(props.get(propName));
        } catch (Exception e) {
            outputAccessFailure(e, propName);
            return defaultValue;
        }
    }

    /**
     * Gets the default Beta Category ID.
     * 
     * @return the Beta Category ID as a string.
     */
    public String getDefaultBetaAppCategoryId() {
        return defaultBetaAppCategoryId;
    }

    /**
     * @return the contextClickEnabled
     */
    public boolean isContextClickEnabled() {
        return contextClickEnabled;
    }

    /**
     * @return the defaultTrashAppCategoryId
     */
    public String getDefaultTrashAppCategoryId() {
        return defaultTrashAppCategoryId;
    }

    /**
     * @return the URL to hit when sending keepalive requests.
     */
    public String getKeepaliveTarget() {
        return keepaliveTarget;
    }

    /**
     * @return the number of minutes between keepalive requests.
     */
    public int getKeepaliveInterval() {
        return keepaliveInterval;
    }

}
