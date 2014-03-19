package org.iplantc.admin.belphegor.client.models;

import org.iplantc.de.commons.client.ErrorHandler;

import com.sencha.gxt.core.shared.FastMap;

import java.util.Arrays;
import java.util.Map;

public class ToolIntegrationAdminProperties {

    private static ToolIntegrationAdminProperties instance;
    private FastMap<String> serviceUrlMap;

    /**
     * The prefix used in each of the property names.
     */
    private static final String PROPERTY_NAME_PREFIX = "org.iplantc.belphegor."; //$NON-NLS-1$

    /**
     * The base URL used to access the bootstrap.
     */
    private static final String SERVICE_URL_BOOTSTRAP = PROPERTY_NAME_PREFIX + "bootstrap"; //$NON-NLS-1$

    /**
     * The base URL used to access the services.
     */
    private static final String SERVICE_URL_BASE = PROPERTY_NAME_PREFIX + "conrad-base"; //$NON-NLS-1$

    /**
     * The URL used to access the App Groups service.
     */
    private static final String SERVICE_URL_CATEGORY_LIST = PROPERTY_NAME_PREFIX + "get-app-groups"; //$NON-NLS-1$
    /**
     * The URL used to access the App Groups service.
     */
    private static final String SERVICE_URL_CATEGORY_LIST_SEC = PROPERTY_NAME_PREFIX + "app-groups"; //$NON-NLS-1$

    /**
     * The URL used to access the Apps by Group service.
     */
    private static final String SERVICE_URL_CATEGORY_APPS = PROPERTY_NAME_PREFIX + "get-apps-in-group"; //$NON-NLS-1$

    /**
     * The URL used to access the Add Category service.
     */
    private static final String SERVICE_URL_CATEGORY_ADD = PROPERTY_NAME_PREFIX + "add-category"; //$NON-NLS-1$

    /**
     * The URL used to access the Rename Category service.
     */
    private static final String SERVICE_URL_CATEGORY_RENAME = PROPERTY_NAME_PREFIX + "rename-category"; //$NON-NLS-1$

    /**
     * The URL used to access the Move Category service.
     */
    private static final String SERVICE_URL_CATEGORY_MOVE = PROPERTY_NAME_PREFIX + "move-category"; //$NON-NLS-1$

    /**
     * The URL used to access the Delete Category service.
     */
    private static final String SERVICE_URL_CATEGORY_DELETE = PROPERTY_NAME_PREFIX + "delete-category"; //$NON-NLS-1$

    /**
     * The URL used to access the Update App service.
     */
    private static final String SERVICE_URL_APP_UPDATE = PROPERTY_NAME_PREFIX + "update-app"; //$NON-NLS-1$

    /**
     * The URL used to access the Move App service.
     */
    private static final String SERVICE_URL_APP_MOVE = PROPERTY_NAME_PREFIX + "move-app"; //$NON-NLS-1$

    /**
     * The URL used to access the restore App service.
     */
    private static final String SERVICE_URL_APP_RESTORE = PROPERTY_NAME_PREFIX + "restore-app"; //$NON-NLS-1$

    /**
     * The URL used to access the Delete App service.
     */
    private static final String SERVICE_URL_APP_DELETE = PROPERTY_NAME_PREFIX + "delete-app"; //$NON-NLS-1$

    /**
     * The URL used to access the categorize App service.
     */
    private static final String SERVICE_URL_APP_CATEGORIZE = PROPERTY_NAME_PREFIX + "categorize-app"; //$NON-NLS-1$

    /**
     * The URL used to access the App details service.
     */
    private static final String SERVICE_URL_APP_DETAILS = PROPERTY_NAME_PREFIX + "app-details"; //$NON-NLS-1$

    /**
     * The URL used to access the App Search service.
     */
    private static final String SERVICE_URL_APP_SEARCH = PROPERTY_NAME_PREFIX + "search-apps"; //$NON-NLS-1$

    /**
     * The URL used to access the add ref genome service.
     */
    private static final String SERVICE_ADD_REF_GENOME = PROPERTY_NAME_PREFIX + "add-ref-genome"; //$NON-NLS-1$

    /**
     * The URL used to access the edit ref genome service.
     */
    private static final String SERVICE_EDIT_REF_GENOME = PROPERTY_NAME_PREFIX + "edit-ref-genome"; //$NON-NLS-1$

    /**
     * The URL used to access the list ref genome service
     */
    private static final String SERVICE_LIST_REF_GENOME = PROPERTY_NAME_PREFIX + "get-ref-genomes"; //$NON-NLS-1$

    /**
     * The URL used to access the list of tool requests.
     */
    private static final String SERVICE_LIST_TOOL_REQUESTS = PROPERTY_NAME_PREFIX + "get-tool-requests"; //$NON-NLS-1$

    /**
     * The URL used to access the Tool Request endpoint.
     */
    private static final String SERVICE_TOOL_REQUEST = PROPERTY_NAME_PREFIX + "tool-request"; //$NON-NLS-1$

    /**
     * The URL used to access the admin system message endpoint.
     */
    private static final String SERVICE_SYSTEM_MESSAGES = PROPERTY_NAME_PREFIX + "notifications.system"; //$NON-NLS-1$  

    private static final String SERVICE_SYSTEM_MESSAGE_TYPES = PROPERTY_NAME_PREFIX + "notifications.system-types"; //$NON-NLS-1$

    /**
     * Properties key of the default Beta Category ID.
     */
    private static final String CATEGORY_DEFAULT_BETA_GROUP_ID = PROPERTY_NAME_PREFIX
            + "category.defaultBetaAnalysisGroupId";//$NON-NLS-1$

    /**
     * Properties key of the default Beta Category ID.
     */
    private static final String CATEGORY_DEFAULT_TRASH_GROUP_ID = PROPERTY_NAME_PREFIX
            + "category.defaultTrashAnalysisGroupId";//$NON-NLS-1$

    /**
     * The property name prefix for CAS session keepalive settings.
     */
    private static final String KEEPALIVE_PREFIX = PROPERTY_NAME_PREFIX + "keepalive."; //$NON-NLS-1$

    /**
     * The name of property containing the CAS session keepalive target URL.
     */
    private static final String KEEPALIVE_TARGET = KEEPALIVE_PREFIX + "target"; //$NON-NLS-1$

    /**
     * The name of the property containing the CAS session keepalive interval.
     */
    private static final String KEEPALIVE_INTERVAL = KEEPALIVE_PREFIX + "interval"; //$NON-NLS-1$

    private static final String APP_DOC_URL = PROPERTY_NAME_PREFIX + "validAppWikiUrlPath"; //$NON-NLS-1$

    /**
     * Properties key of the context click enabled option
     */
    private static final String CONTEXT_CLICK_ENABLED = PROPERTY_NAME_PREFIX + "contextMenu.enabled";//$NON-NLS-1$

    private String defaultBetaAnalysisGroupId;

    private String defaultTrashAnalysisGroupId;

    private boolean contextClickEnabled;

    private String keepaliveTarget;

    private int keepaliveInterval;

    private String[] validAppWikiUrlPath;

    public static ToolIntegrationAdminProperties getInstance() {

        if (instance == null) {
            instance = new ToolIntegrationAdminProperties();
        }
        return instance;
    }

    /**
     * Initializes this class from the given set of properties.
     * 
     * @param properties the properties that were fetched from the server.
     */
    public void initialize(Map<String, String> properties) {
        serviceUrlMap = new FastMap<String>();

        for (String key : Arrays.asList(SERVICE_URL_BASE, SERVICE_URL_CATEGORY_ADD,
                SERVICE_URL_CATEGORY_RENAME, SERVICE_URL_CATEGORY_MOVE, SERVICE_URL_CATEGORY_DELETE,
                SERVICE_URL_CATEGORY_LIST, SERVICE_URL_CATEGORY_LIST_SEC, SERVICE_URL_CATEGORY_APPS,
                SERVICE_URL_APP_UPDATE, SERVICE_URL_APP_MOVE, SERVICE_URL_APP_DELETE,
                SERVICE_URL_APP_RESTORE, SERVICE_URL_APP_CATEGORIZE, SERVICE_URL_APP_DETAILS,
                SERVICE_URL_APP_SEARCH, SERVICE_ADD_REF_GENOME, SERVICE_EDIT_REF_GENOME,
                SERVICE_LIST_REF_GENOME, SERVICE_URL_BOOTSTRAP, SERVICE_LIST_TOOL_REQUESTS,
                SERVICE_TOOL_REQUEST, SERVICE_SYSTEM_MESSAGES, SERVICE_SYSTEM_MESSAGE_TYPES)) {
            serviceUrlMap.put(key, properties.get(key));
        }

        setDefaultTrashAnalysisGroupId(properties.get(CATEGORY_DEFAULT_TRASH_GROUP_ID));

        contextClickEnabled = getBooleanProperty(properties, CONTEXT_CLICK_ENABLED, false);
        keepaliveInterval = getIntProperty(properties, KEEPALIVE_INTERVAL, -1);

        defaultBetaAnalysisGroupId = getStringProperty(properties, CATEGORY_DEFAULT_BETA_GROUP_ID, "");
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

    public String getBootStrapUrl() {
        return serviceUrlMap.get(SERVICE_URL_BOOTSTRAP);
    }

    /**
     * Gets the Add ref genome service URL.
     * 
     * @return the URL as a string.
     */
    public String getAddRefGenomeServiceUrl() {
        return serviceUrlMap.get(SERVICE_ADD_REF_GENOME);
    }

    /**
     * Gets the edit ref genome service URL.
     * 
     * @return the URL as a string.
     */
    public String getEditRefGenomeServiceUrl() {
        return serviceUrlMap.get(SERVICE_EDIT_REF_GENOME);
    }

    /**
     * Gets the list ref genomes service URL.
     * 
     * @return the URL as a string.
     */
    public String getListRefGenomeServiceUrl() {
        return serviceUrlMap.get(SERVICE_LIST_REF_GENOME);
    }

    /**
     * Gets the Add Category service URL.
     * 
     * @return the URL as a string.
     */
    public String getAddCategoryServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_ADD);
    }

    /**
     * Gets the List Tool Requests service URL.
     * 
     * @return the URL as a string.
     */
    public String getListToolRequestsServiceUrl() {
        return serviceUrlMap.get(SERVICE_LIST_TOOL_REQUESTS);
    }

    /**
     * Gets the Tool Request service URL.
     * 
     * @return the URL as a string.
     */
    public String getToolRequestServiceUrl() {
        return serviceUrlMap.get(SERVICE_TOOL_REQUEST);
    }

    /**
     * Gets the Rename Category service URL.
     * 
     * @return the URL as a string.
     */
    public String getRenameCategoryServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_RENAME);
    }

    /**
     * Gets the Move Category service URL.
     * 
     * @return the URL as a string.
     */
    public String getMoveCategoryServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_MOVE);
    }

    /**
     * Gets the Delete Category service URL.
     * 
     * @return the URL as a string.
     */
    public String getDeleteCategoryServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_DELETE);
    }

    /**
     * Gets the Category Listing service URL.
     * 
     * @return the URL as a string.
     */
    public String getCategoryListServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_LIST);
    }

    public String getCategoryListSecuredServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_LIST_SEC);
    }

    /**
     * Gets the Apps-by-Category service URL.
     * 
     * @return the URL as a string.
     */
    public String getAppsInCategoryServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_CATEGORY_APPS);
    }

    /**
     * Gets the Update App service URL.
     * 
     * @return the URL as a string.
     */
    public String getUpdateAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_UPDATE);
    }

    /**
     * Gets the Move App service URL.
     * 
     * @return the URL as a string.
     */
    public String getMoveAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_MOVE);
    }

    /**
     * Gets the Delete App service URL.
     * 
     * @return the URL as a string.
     */
    public String getDeleteAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_DELETE);
    }

    /**
     * Gets the Restore App service URL.
     * 
     * @return the URL as a string.
     */
    public String getRestoreAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_RESTORE);
    }

    /**
     * Gets the Categorize App service URL.
     * 
     * @return the URL as a string.
     */
    public String getCategorizeAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_CATEGORIZE);
    }

    /**
     * Gets the App Details service URL.
     * 
     * @return the URL as a string.
     */
    public String getAppDetailsServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_DETAILS);
    }

    /**
     * Gets the Search App service URL.
     * 
     * @return the URL as a string.
     */
    public String getSearchAppServiceUrl() {
        return serviceUrlMap.get(SERVICE_URL_APP_SEARCH);
    }

    /**
     * Gets the default Beta Category ID.
     * 
     * @return the Beta Category ID as a string.
     */
    public String getDefaultBetaAnalysisGroupId() {
        return defaultBetaAnalysisGroupId;
    }

    /**
     * @return the contextClickEnabled
     */
    public boolean isContextClickEnabled() {
        return contextClickEnabled;
    }

    /**
     * @param defaultTrashAnalysisGroupId the defaultTrashAnalysisGroupId to set
     */
    public void setDefaultTrashAnalysisGroupId(String defaultTrashAnalysisGroupId) {
        this.defaultTrashAnalysisGroupId = defaultTrashAnalysisGroupId;
    }

    /**
     * @return the defaultTrashAnalysisGroupId
     */
    public String getDefaultTrashAnalysisGroupId() {
        return defaultTrashAnalysisGroupId;
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

    /**
     * The path to DE help file
     * 
     * @return path to help file
     */
    public String[] getValidAppWikiUrlPath() {
        return validAppWikiUrlPath;
    }

    /**
     * Gets the Admin System message service URL.
     * 
     * @return the URL as a string.
     */
    public String getAdminSystemMessageServiceUrl() {
        return serviceUrlMap.get(SERVICE_SYSTEM_MESSAGES);
    }

    /**
     * Gets the Admin System message types service URL.
     * 
     * @return the URL as a string.
     */
    public String getAdminSystemMessageTypesUrl() {
        return serviceUrlMap.get(SERVICE_SYSTEM_MESSAGE_TYPES);
    }
    
}
