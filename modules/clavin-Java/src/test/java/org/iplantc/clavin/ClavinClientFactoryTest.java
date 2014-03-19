package org.iplantc.clavin;

import org.iplantc.clavin.files.FilesClavinClient;
import org.iplantc.clavin.zookeeper.ZookeeperClavinClient;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author dennis
 */
public class ClavinClientFactoryTest {

    /**
     * The path to the test {@code zkhosts.properties} file.
     */
    private static final String TEST_ZKHOSTS_PATH = "src/test/resources/zkhosts.properties";

    /**
     * The path to a Zookeeper connection information file that doesn't exist.
     */
    private static final String MISSING_ZKHOSTS_PATH = "src/test/resources/missing-zkhosts.properties";

    /**
     * The path to a malformed Zookeeper connection information file.
     */
    private static final String BOGUS_ZKHOSTS_PATH = "src/test/resources/bogus-zkhosts.properties";

    /**
     * Verifies that we get an instance of FilesClavinClient if the properties file for the service is somewhere on the
     * classpath.
     */
    @Test
    public void testGetFilesClavinClient() {
        ClavinClientFactory factory = ClavinClientFactory.getClavinClientFactory(MISSING_ZKHOSTS_PATH);
        assertTrue(ClavinClientFactory.getClavinClientFactory().getClavinClient("foo") instanceof FilesClavinClient);
    }

    /**
     * Verifies that we get an instance of ZookeeperClavinClient if the properties file for the service is not on the
     * classpath and the Zookeeper connection information file is present.
     */
    @Test
    public void testGetZookeeperClavinClient() {
        ClavinClientFactory factory = ClavinClientFactory.getClavinClientFactory(TEST_ZKHOSTS_PATH);
        assertTrue(factory.getClavinClient("bar") instanceof ZookeeperClavinClient);
    }

    /**
     * Verifies that we get an instance of FilesClavinClient if Zookeeper connection information file and the properties
     * file for the service are both available.
     */
    @Test
    public void testGetFilesClavinClientWhenZookeeperInfoAvailable() {
        ClavinClientFactory factory = ClavinClientFactory.getClavinClientFactory(TEST_ZKHOSTS_PATH);
        assertTrue(factory.getClavinClient("foo") instanceof FilesClavinClient);
    }

    /**
     * Verifies that we get an InvalidZkHostsException if the Zookeeper connection information file is malformed.
     */
    @Test(expected = InvalidZkHostsException.class)
    public void testBogusZookeeperConnectionInfoFile() {
        ClavinClientFactory factory = ClavinClientFactory.getClavinClientFactory(BOGUS_ZKHOSTS_PATH);
        factory.getClavinClient("bar");
    }

    /**
     * Verifies that we get a ClavinClientCreationException if neither the Zookeeper connection information file nor
     * the properties file for the service is available.
     */
    @Test(expected = ClavinClientCreationException.class)
    public void testNoConfigurationAvailable() {
        ClavinClientFactory factory = ClavinClientFactory.getClavinClientFactory(MISSING_ZKHOSTS_PATH);
        factory.getClavinClient("bar");
    }
}
