package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ServiceNotPermittedException;
import org.iplantc.clavin.util.HostUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * Unit tests for {@link ZookeeperClavinClient}.
 *
 * @author Dennis Roberts
 */
public class ZookeeperClavinClientTest {

    /**
     * The name of the deployment to use for testing.
     */
    private static final String DEPLOYMENT = "foo.bar.baz";

    /**
     * The base path for properties associated with our test deployment.
     */
    private static final String DEPLOYMENT_PATH = "/" + DEPLOYMENT.replace('.', '/');

    /**
     * The name of the service to use for testing.
     */
    private static final String SERVICE = "quux";

    /**
     * The mock Zookeeper client to use for the test.
     */
    private MockZkClient zkClient;

    /**
     * The Clavin Zookeeper client to use for the test.
     */
    private ZookeeperClavinClient clavinClient;

    @Before
    public void setUp() {
        zkClient = new MockZkClient();
        clavinClient = new ZookeeperClavinClient(zkClient);
    }

    /**
     * Adds a node to the hosts branch of the mock Zookeeper cluster
     *
     * @param key
     */
    private void addHostNode(String key) {
        String path = ZookeeperClavinClient.HOSTS_BASE + HostUtils.getIpAddress() + "/" + key;
        zkClient.addNode(path, "");
    }

    /**
     * Makes the current host an administrative host.
     */
    private void makeAdminHost() {
        addHostNode(ZookeeperClavinClient.ADMIN_KEY);
    }

    /**
     * Makes the current host a service host.
     */
    private void makeServiceHost() {
        addHostNode(DEPLOYMENT);
    }

    /**
     * Adds a configuration property to our mock Zookeeper cluster for the default service name.
     *
     * @param key the property key.
     * @param value the property value.
     */
    private void addProperty(String key, String value) {
        addProperty(SERVICE, key, value);
    }

    /**
     * Adds a configuration property to our mock Zookeeper cluster.
     *
     * @param serviceName the name of the service.
     * @param key the property key.
     * @param value the property value.
     */
    private void addProperty(String serviceName, String key, String value) {
        zkClient.addNode(DEPLOYMENT_PATH + "/" + serviceName + "/" + key, value);
    }

    /**
     * Verifies that service validation succeeds if services are permitted to run on the local host.  This test passes
     * if no exceptions are thrown.
     */
    @Test
    public void validationShouldPassIfServicesPermitted() {
        makeServiceHost();
        clavinClient.validateService(SERVICE);
    }

    /**
     * Verifies that service validation does not succeed if the current host is configured to be only an administrative
     * host.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void validationShouldFailForDedicatedAdminHost() {
        makeAdminHost();
        clavinClient.validateService(SERVICE);
    }

    /**
     * Verifies that service validation does not succeed if the current host is not authorized for either services or
     * administration.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void validationShouldFailForUnauthorizedHost() {
        clavinClient.validateService(SERVICE);
    }

    /**
     * Verifies that service validation succeeds if the current host is authorized for both services and administration.
     * This test passes if no exceptions are thrown.
     */
    @Test
    public void validationShouldPassForCombinedAdminAndServiceHost() {
        makeAdminHost();
        makeServiceHost();
        clavinClient.validateService(SERVICE);
    }

    /**
     * Verifies that we get a ServiceNotPermittedException if we try to load properties from a host that is neither
     * authorized for services nor administration.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void propertyLoadingShouldFailForUnauthorizedHost() {
        clavinClient.loadProperties(SERVICE);
    }

    /**
     * Verifies that we get a ServiceNotPermittedException if we try to load properties from a host that is configured
     * to be only an administrative host.
     */
    @Test(expected = ServiceNotPermittedException.class)
    public void propertyLoadingShouldFailForDedicatedAdminHost() {
        makeAdminHost();
        clavinClient.loadProperties(SERVICE);
    }

    /**
     * Verifies that property loading works if we try to load properties from a host that is configured to be a service
     * host.
     */
    @Test
    public void propertyLoadingShouldWorkForServiceHost() {
        makeServiceHost();
        assertNotNull(clavinClient.loadProperties(SERVICE));
    }

    /**
     * Verifies that property loading works if we try to load properties from a host that is configured to be both a
     * service host and an administrative host.
     */
    @Test
    public void propertyLoadingShouldWorkForCombinedServiceAndAdminHost() {
        makeServiceHost();
        makeAdminHost();
        assertNotNull(clavinClient.loadProperties(SERVICE));
    }

    /**
     * Verifies that properties are actually loaded for
     */
    @Test
    public void propertyLoadingShouldActuallyLoadProperties() {
        makeServiceHost();
        addProperty("some.property", "some-property-value");
        addProperty("some.other.property", "some-other-property-value");
        Properties props = clavinClient.loadProperties(SERVICE);
        assertNotNull(props);
        assertEquals("some-property-value", props.getProperty("some.property"));
        assertEquals("some-other-property-value", props.getProperty("some.other.property"));
    }

    /**
     * Verifies that properties that are intended for another service are not loaded.
     */
    @Test
    public void propertyLoadingShouldNotLoadPropertiesForOtherSerivces() {
        makeServiceHost();
        addProperty("some.property", "some-property-value");
        addProperty("some.other.property", "some-other-property-value");
        addProperty("glarb", "foriegn.property", "foreign-property-value");
        Properties props = clavinClient.loadProperties(SERVICE);
        assertNotNull(props);
        assertEquals("some-property-value", props.getProperty("some.property"));
        assertEquals("some-other-property-value", props.getProperty("some.other.property"));
        assertNull(props.getProperty("foreign.property"));
    }
}
