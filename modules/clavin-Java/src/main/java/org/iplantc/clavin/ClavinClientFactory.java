package org.iplantc.clavin;

import org.iplantc.clavin.files.FilesClavinClient;
import org.iplantc.clavin.zookeeper.ZkClient;
import org.iplantc.clavin.zookeeper.ZkClientImpl;
import org.iplantc.clavin.zookeeper.ZookeeperClavinClient;

import com.google.common.io.Closeables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A factory used to create {@link ClavinClient} instances.
 *
 * @author Dennis Roberts
 */
public class ClavinClientFactory {

    /**
     * The default path to the file containing the Zookeeper connection information.
     */
    public static final String DEFAULT_ZK_HOSTS_PATH = "/etc/iplant-services/zkhosts.properties";

    /**
     * The path to the file containing the Zookeeper connection information.
     */
    private String zkHostsPath;

    /**
     * The number of times to retry failed Zookeeper operations.
     */
    private int retryCount;

    /**
     * The number of milliseconds to sleep between Zookeeper operation attempts.
     */
    private int sleepBetweenTries;

    /**
     * The character encoding to use when retrieving information from Zookeeper.
     */
    private String encoding;

    // This class can only be instantiated by the builder.
    private ClavinClientFactory() {}

    /**
     * Builds a Clavin client factory with all of the default settings.
     *
     * @return the Clavin client factory.
     */
    public static ClavinClientFactory getClavinClientFactory() {
        return new Builder().build();
    }

    /**
     * Builds a Clavin client factory with the specified path to the Zookeeper connection information file.  This is
     * the most complex configuration option provided by static factory methods in this class.  For more customized
     * configuration, please see the {@link Builder} documentation.
     *
     * @param zkHostsPath the path to the file containing the Zookeeper connection information.
     * @return the Clavin client factory.
     */
    public static ClavinClientFactory getClavinClientFactory(String zkHostsPath) {
        return new Builder().setZkHostsPath(zkHostsPath).build();
    }

    /**
     * Gets the Clavin client for the given service name.  If the properties file for the service is present anywhere
     * on the classpath then an instance of FilesClavinClient will be created.  If the properties file is not available
     * but the Zookeeper connection information file is available then an instance of ZookeeperClavinClient will be
     * created.  If neither the properties file for the service nor the Zookeeper connection information file is
     * available then a ClavinClientCreationException is thrown.
     *
     * @param serviceName the name of the service.
     * @return the Clavin client.
     */
    public ClavinClient getClavinClient(String serviceName) {
        if (propertiesFileInClasspath(serviceName)) {
            return new FilesClavinClient();
        }
        else if (new File(zkHostsPath).isFile()) {
            return createZkClavinClient();
        }
        throw new ClavinClientCreationException(serviceName, zkHostsPath);
    }

    /**
     * Determines if a properties file for the given service name exists anywhere on the classpath.
     *
     * @param serviceName the name of the service.
     * @return true if the properties file does exist on the classpath.
     */
    private boolean propertiesFileInClasspath(String serviceName) {
        return Thread.currentThread().getContextClassLoader().getResource(serviceName + ".properties") != null;
    }

    /**
     * Creates a Clavin client that retrieves configuration settings from Zookeeper.
     *
     * @return the Clavin client.
     */
    private ClavinClient createZkClavinClient() {
        String connectionSpec = retrieveConnectionSpec();
        ZkClient client = new ZkClientImpl(connectionSpec, retryCount, sleepBetweenTries, encoding);
        return new ZookeeperClavinClient(client);
    }

    /**
     * Retrieves the connection specification string from the Zookeeper connection information file.
     *
     * @return the connection specification string.
     */
    private String retrieveConnectionSpec() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(zkHostsPath);
            Properties props = new Properties();
            props.load(in);
            String connectionSpec = props.getProperty("zookeeper");
            if (connectionSpec == null) {
                throw new InvalidZkHostsException(zkHostsPath);
            }
            return connectionSpec;
        }
        catch (IOException e) {
            throw new ZkHostsReadException(zkHostsPath, e);
        }
        finally {
            if (in != null) {
                Closeables.closeQuietly(in);
            }
        }
    }

    /**
     * Used to build ClavinClientFactory instances.  The setters for this builder class all return a reference to
     * the builder so that they can be called in sequence.  For example:
     *
     * <pre><code>
     *    new ClavinClientFactory.Builder()
     *        .setZkHostsPath("/path/to/zkhosts.properties")
     *        .setRetryCount(10)
     *        .setsleepBetweenRetries(1000)
     *        .setEncoding("ISO-8859-1")
     *        .build();
     * </code></pre>
     */
    public static class Builder {

        /**
         * The path to the file containing the Zookeeper connection information.
         */
        private String zkHostsPath = DEFAULT_ZK_HOSTS_PATH;

        /**
         * The number of times to retry failed Zookeeper operations.
         */
        private int retryCount = ZkClientImpl.DEFAULT_RETRY_COUNT;

        /**
         * The number of milliseconds to sleep between Zookeeper operation attempts.
         */
        private int sleepBetweenTries = ZkClientImpl.DEFAULT_SLEEP_BETWEEN_TRIES;

        /**
         * The character encoding to use when retrieving data from Zookeeper.
         */
        private String encoding = ZkClientImpl.DEFAULT_ENCODING;

        /**
         * Sets the path to the file containing the Zookeeper connection information.  The default value for this
         * parameter is defined by {@link ClavinClientFactory}.DEFAULT_ZK_HOSTS_PATH.
         *
         * @param zkHostsPath the path to the file containing the Zookeeper connection information.
         * @return the builder.
         */
        public Builder setZkHostsPath(String zkHostsPath) {
            this.zkHostsPath = zkHostsPath;
            return this;
        }

        /**
         * Sets the number of times to retry failed Zookeeper operations.  The default value for this parameter is
         * defined by {@link ZkClientImpl}.DEFAULT_RETRY_COUNT.
         *
         * @param retryCount the number of times to retry failed Zookeeper operations.
         * @return the builder.
         */
        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        /**
         * Sets the number of milliseconds to sleep before retrying a failed Zookeeper operation.  The default value
         * for this parameter is defined by {@link ZkClientImpl}.DEFAULT_SLEEP_BETWEEN_TRIES;
         *
         * @param sleepBetweenTries the number of milliseconds to sleep before retrying a failed Zookeeper operation.
         * @return the builder.
         */
        public Builder setSleepBetweenTries(int sleepBetweenTries) {
            this.sleepBetweenTries = sleepBetweenTries;
            return this;
        }

        /**
         * Sets the character encoding to use when retrieving data from Zookeeper.  The default value for this
         * parameter is defined by {@link ZkClientImpl}.DEFAULT_ENCODING.
         *
         * @param encoding the character encoding to use when retrieving data from Zookeeper.
         * @return the builder.
         */
        public Builder setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * Builds a Clavin client factory using the settings stored in the builder.
         *
         * @return the Clavin client factory.
         */
        public ClavinClientFactory build() {
            ClavinClientFactory factory = new ClavinClientFactory();
            factory.zkHostsPath = zkHostsPath;
            factory.retryCount = retryCount;
            factory.sleepBetweenTries = sleepBetweenTries;
            factory.encoding = encoding;
            return factory;
        }
    }
}
