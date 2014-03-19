package org.iplantc.clavin;

import java.util.Properties;

/**
 * An interface that can be used to obtain configuration settings.  These configuration settings may be obtained from
 * various places depending on which implementation of this interface is being used.
 * 
 * @author Dennis Roberts
 */
public interface ClavinClient {

    /**
     * Determines whether or not a service can run on the current host.
     * 
     * @param serviceName the name of the service.
     * @throws ServiceNotPermittedException if the service is not permitted to run on the current host.
     */
    public void validateService(String serviceName) throws ServiceNotPermittedException;

    /**
     * Loads the configuration settings for a service.
     * 
     * @param serviceName the name of the service to load the settings for.
     * @return the configuration settings.
     * @throws ClavinException if the configuration can't be retrieved.
     */
    public Properties loadProperties(String serviceName) throws ClavinException;
}
