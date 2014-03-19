package org.iplantc.clavin.files;

import org.iplantc.clavin.ClavinClient;
import org.iplantc.clavin.ClavinException;
import org.iplantc.clavin.PropertyLoadException;
import org.iplantc.clavin.ServiceNotPermittedException;
import org.iplantc.clavin.util.HostUtils;
import org.iplantc.clavin.util.IpAddressNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A Clavin client implementation that retrieves configuration settings from properties files.
 * 
 * @author Dennis Roberts
 */
public class FilesClavinClient implements ClavinClient {

    /**
     * Verifies that a service can run on the current host.  When configuration settings are being loaded from files,
     * the service can only run if the file is present on the classpath.
     * 
     * @param serviceName the name of the service.
     * @throws ServiceNotPermittedException if the service can't run on the current host.
     * @throws IpAddressNotFoundException if the IP address of the local host can't be found.
     * @throws FileCloseException if the properties file was found and opened but could not be closed.
     */
    public void validateService(String serviceName) throws ClavinException {
        try {
            inputStreamForService(serviceName).close();
        }
        catch (IOException e) {
            throw new FileCloseException(e);
        }
    }

    /**
     * Loads the configuration settings for a service.
     * 
     * @param serviceName the name of the service.
     * @return a {@link Properties} instance containing the configuration settings.
     * @throws PropertyLoadException if the properties can't be loaded.
     */
    public Properties loadProperties(String serviceName) throws ClavinException {
        try {
            Properties props = new Properties();
            InputStream in = inputStreamForService(serviceName);
            props.load(in);
            in.close();
            return props;
        }
        catch (IOException e) {
            throw new PropertyLoadException(e);
        }
    }

    /**
     * Obtains an input stream that can be used to load the properties for a service.
     * 
     * @param serviceName the name of the service.
     * @return the input stream.
     * @throws ServiceNotPermittedException if the service can't run on the current host.
     */
    private InputStream inputStreamForService(String serviceName) throws ServiceNotPermittedException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filenameFor(serviceName));
        if (in == null) {
            throw new ServiceNotPermittedException(serviceName, HostUtils.getIpAddress());
        }
        return in;
    }

    /**
     * Returns the file name to use for a service.
     * 
     * @param serviceName the name of the service.
     * @return the file name.
     * @throws IllegalArgumentException if the service name is null.
     */
    private String filenameFor(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("no service name provided");
        }
        return serviceName + ".properties";
    }
}
