package org.iplantc.clavin.files;

import org.iplantc.clavin.ServiceNotPermittedException;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 *
 * @author dennis
 */
public class FilesClavinClientTest {

    /**
     * The Clavin client.
     */
    private FilesClavinClient client;
    
    @Before
    public void setUp() {
        client = new FilesClavinClient();
    }
    
    /**
     * Verifies that a service can run if its properties file exists on the classpath.  This test passes if no
     * exception is thrown.
     */
    @Test
    public void serviceCanRunIfPropertiesFileFound() {
        client.validateService("foo");
    }

    /**
     * Verifies that a service can't run if its properties file does not exist on the classpath.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void serviceCannotRunIfPropertiesFileNotFound() {
        client.validateService("bar");
    }

    /**
     * Verifies that we get an IllegalArgumentException if the service name is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validationShouldCheckForMissingServiceName() {
        client.validateService(null);
    }

    /**
     * Verifies that we can load the configuration settings if the properties file exists on the classpath.
     */
    @Test
    public void canLoadConfigurationIfPropertiesFileFound() {
         Properties props = client.loadProperties("foo");
         assertEquals("baz", props.getProperty("bar"));
    }

    /**
     * Verifies that we get a ServiceNotPermittedException if we try to load properties and the properties file
     * does not exist on the classpath.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void loadPropertiesThrowsServiceNotPermittedExceptionIfPropertiesFileNotFound() {
        client.loadProperties("bar");
    }

    /**
     * Verifies that we get an IllegalArgumentException if we pass a null service name into loadProperties.
     */
    @Test(expected = IllegalArgumentException.class)
    public void loadPropertiesShouldCheckForMissingServiceName() {
        client.loadProperties(null);
    }
}
