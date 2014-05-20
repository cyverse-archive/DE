package org.iplantc.de.server;

import org.iplantc.clavin.spring.ClavinPropertyPlaceholderConfigurer;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * Provides access to configuration properties for the Discovery Environment.
 * 
 * @author Donald A. Barre
 */
public class DiscoveryEnvironmentProperties {

    // Constants used to obtain property values.
    private static final String DATA_MGMT_SERVICE_BASE_URL = "org.iplantc.services.de-data-mgmt.base"; // $NON-NLS$
    private static final String FILE_IO_PREFIX = "org.iplantc.services.file-io.";                      // $NON-NLS$
    private static final String FILE_IO_BASE_URL = FILE_IO_PREFIX + "base.secured";                    // $NON-NLS$
    private static final String UNPROTECTED_FILE_IO_BASE_URL = FILE_IO_PREFIX + "base.unsecured";      // $NON-NLS$
    private static final String PREFIX = "org.iplantc.discoveryenvironment";                           // $NON-NLS$
    private static final String DE_DEFAULT_BUILD_NUMBER = PREFIX + ".about.defaultBuildNumber";        // $NON-NLS$
    private static final String DE_RELEASE_VERSION = PREFIX + ".about.releaseVersion";                 // $NON-NLS$
    private static final String DE_BASE_URL = PREFIX + ".cas.server-name";                             // $NON-NLS$
    private static final String MULE_SERVICE_BASE_URL = PREFIX + ".muleServiceBaseUrl";                // $NON-NLS$
    private static final String PRODUCTION_DEPLOYMENT = PREFIX + ".environment.prod-deployment";       // $NON-NLS$
    private static final String MAINTENANCE_FILE = PREFIX + ".maintenance-file";                       // $NON-NLS$

    /**
     * The list of required properties.
     */
    private static final String[] REQUIRED_PROPERTIES = {MULE_SERVICE_BASE_URL,
            DATA_MGMT_SERVICE_BASE_URL, FILE_IO_BASE_URL, UNPROTECTED_FILE_IO_BASE_URL,
            PRODUCTION_DEPLOYMENT, MAINTENANCE_FILE, DE_BASE_URL};

    /**
     * The configuration properties.
     */
    private final Properties props;

    /**
     * @param configurer the configurer that was used to load the properties.
     */
    public DiscoveryEnvironmentProperties(ClavinPropertyPlaceholderConfigurer configurer) {
        if (configurer == null) {
            throw new IllegalArgumentException("the configurer may not be null"); // $NON-NLS$
        }
        props = configurer.getConfig("discoveryenvironment"); // $NON-NLS$
        if (props == null) {
            throw new IllegalArgumentException("discovery environment configuration not found"); // $NON-NLS$
        }
        validateProperties();
    }

    /**
     * @param props the configuration properties.
     */
    public DiscoveryEnvironmentProperties(Properties props) {
        if (props == null) {
            throw new IllegalArgumentException("the properties may not be null"); // $NON-NLS$
        }
        this.props = props;
        validateProperties();
    }

    /**
     * Gets the discovery environment properties to use for the given servlet context.
     * 
     * @param context the servlet context.
     * @return the discovery environment properties.
     * @throws IllegalStateException if the discovery environment properties aren't defined.
     */
    public static DiscoveryEnvironmentProperties getDiscoveryEnvironmentProperties(ServletContext context) {
        WebApplicationContext appContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(context);
        DiscoveryEnvironmentProperties result
                = (DiscoveryEnvironmentProperties) appContext.getBean(DiscoveryEnvironmentProperties.class);
        if (result == null) {
            throw new IllegalStateException("discovery environment properties bean not defined"); // $NON-NLS$
        }
        return result;
    }

    /**
     * Validates that we have values for all required properties.
     */
    private void validateProperties() {
        for (int i = 0; i < REQUIRED_PROPERTIES.length; i++) {
            String propertyName = REQUIRED_PROPERTIES[i];
            String propertyValue = props.getProperty(propertyName);
            if (propertyValue == null || propertyValue.equals("")) {
                throw new ExceptionInInitializerError("missing required property: " + propertyName); // $NON-NLS$
            }
        }
    }

    /**
     * Gets the default build number.
     * 
     * When a build number is not available, this value will be provided.
     * 
     * @return a string representing the default build number.
     */
    public String getDefaultBuildNumber() {
        return props.getProperty(DE_DEFAULT_BUILD_NUMBER);
    }

    /**
     * Gets the release version for the Discovery Environment.
     * 
     * This will be displayed in about text or provided as context.
     * 
     * @return a string representing the release version of the Discovery Environment.
     */
    public String getReleaseVersion() {
        return props.getProperty(DE_RELEASE_VERSION);
    }

    /**
     * Gets the base URL used to connect to the Discovery Environment. This is always the same as the CAS server
     * name.
     *
     * @return the URL as a string.
     */
    public String getDeBaseUrl() { return props.getProperty(DE_BASE_URL); }

    /**
     * Gets the base data management URL.
     * 
     * @return the URL as a string.
     */
    public String getDataMgmtServiceBaseUrl() {
        return props.getProperty(DATA_MGMT_SERVICE_BASE_URL);
    }

    /**
     * Gets the base URL of the file I/O services.
     * 
     * @return the URL as a string.
     */
    public String getFileIoBaseUrl() {
        return props.getProperty(FILE_IO_BASE_URL);
    }

    /**
     * Gets the base URL of the unprotected file I/O services.
     * 
     * @return the URL as a string.
     */
    public String getUnprotectedFileIoBaseUrl() {
        return props.getProperty(UNPROTECTED_FILE_IO_BASE_URL);
    }

    /**
     * Gets the base URL for protected donkey end-points
     * 
     * @return the URL as a String.
     */
    public String getProtectedDonkeyBaseUrl() {
        return props.getProperty(MULE_SERVICE_BASE_URL);
    }

    /**
     * @return true if the current deployment is configured to be a production deployment.
     */
    public boolean isProduction() {
        return Boolean.parseBoolean(props.getProperty(PRODUCTION_DEPLOYMENT));
    }

    /**
     * @return the path to the maintenance file.
     */
    public String getMaintenanceFile() {
        return props.getProperty(MAINTENANCE_FILE);
    }
}
