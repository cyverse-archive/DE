package org.iplantc.clavin.spring;

import org.iplantc.clavin.ClavinClient;
import org.iplantc.clavin.ClavinClientFactory;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A property placeholder configurer that loads properties using a Clavin client.
 *
 * @author Dennis Roberts
 */
public class ClavinPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /**
     * The name of the service.
     */
    private String serviceName;

    /**
     * The names of the configurations to load in addition to the service name itself.
     */
    private List<String> configNames;

    /**
     * A map of names to configurations, available only after the properties have been loaded.
     */
    private Map<String, Properties> configs = new HashMap<String, Properties>();

    /**
     * @param serviceName the name of the service.
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @param configNames the names of the configurations to load in addition to the service name itself.
     */
    public void setConfigNames(List<String> configNames) {
        this.configNames = configNames;
    }

    /**
     * @param configName the name of the configuration to retrieve.
     * @return a map of names to configurations, available only after the properties have been loaded.
     */
    public Properties getConfig(String configName) {
        return configs.get(configName);
    }

    /**
     * Loads the properties using a Clavin client.
     *
     * @param props the properties that are being loaded.
     * @throws IOException
     */
    @Override
    protected void loadProperties(Properties props) throws IOException {
        Assert.notNull(serviceName, "no service name specified");
        ClavinClient client = ClavinClientFactory.getClavinClientFactory().getClavinClient(serviceName);
        CollectionUtils.mergePropertiesIntoMap(loadConfig(client, serviceName), props);
        if (configNames != null) {
            for (String configName : configNames) {
                CollectionUtils.mergePropertiesIntoMap(loadConfig(client, configName), props);
            }
        }
    }

    /**
     * Loads and caches a configuration using a Clavin client.
     *
     * @param client the Clavin client.
     * @param configName the name of the configuration.
     * @return the configuration settings as a {@link Properties} instance.
     */
    private Properties loadConfig(ClavinClient client, String configName) {
        Properties props = client.loadProperties(configName);
        configs.put(configName, props);
        return props;
    }
}
