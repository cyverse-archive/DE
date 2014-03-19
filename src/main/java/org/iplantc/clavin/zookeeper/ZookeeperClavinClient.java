package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinClient;
import org.iplantc.clavin.ClavinException;
import org.iplantc.clavin.ServiceNotPermittedException;
import org.iplantc.clavin.util.HostUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Properties;

/**
 * A Clavin client implementation that retrieves configuration settings from Zookeeper.
 *
 * @author Dennis Roberts
 */
public class ZookeeperClavinClient implements ClavinClient {

    /**
     * The base path for host access control lists.
     */
    public static final String HOSTS_BASE = "/hosts/";

    /**
     * The key used to indicate that a node can serve as an administrative node.
     */
    public static final String ADMIN_KEY = "admin";

    /**
     * The Zookeeper client.
     */
    private ZkClient client;

    /**
     * @param client the Zookeeper client.
     */
    public ZookeeperClavinClient(ZkClient client) {
        this.client = client;
    }

    /**
     * @return the deployment string to use for the current host.
     */
    private String getDeployment() {
        final Iterable<String> children = client.getChildren(HOSTS_BASE + HostUtils.getIpAddress());
        return Iterables.find(children, new Predicate<String>() {
            public boolean apply(String t) {
                return !t.equals(ADMIN_KEY);
            }
        }, null);
    }

    /**
     * Validates the given deployment by ensuring that it's not null.
     *
     * @param serviceName the name of the service.
     * @param deployment the deployment to validate.
     * @throws ServiceNotPermittedException if the service is not permitted to run on the current host.
     */
    private void validateDeployment(String serviceName, String deployment) throws ServiceNotPermittedException {
        if (getDeployment() == null) {
            throw new ServiceNotPermittedException(serviceName, HostUtils.getIpAddress());
        }
    }

    /**
     * Verifies that services are permitted to run on the current host.
     *
     * @param serviceName the name of the service.
     * @throws ServiceNotPermittedException if the service is not permitted to run on the current host.
     */
    public void validateService(String serviceName) throws ServiceNotPermittedException {
        try {
            client.connect();
            validateDeployment(serviceName, getDeployment());
        }
        finally {
            client.disconnect();
        }
    }

    /**
     * Loads the configuration properties for a service.
     *
     * @param serviceName the name of the service.
     * @return the configuration properties.
     * @throws ClavinException if the properties can't be loaded.
     */
    public Properties loadProperties(String serviceName) throws ClavinException {
        try {
            client.connect();
            final String deployment = getDeployment();
            validateDeployment(serviceName, deployment);
            final String path = "/" + deployment.replace('.', '/') + "/" + serviceName;
            Properties props = new Properties();
            for (String key : client.getChildren(path)) {
                props.setProperty(key, client.readNode(path, key));
            }
            return props;
        }
        finally {
            client.disconnect();
        }
    }
}
