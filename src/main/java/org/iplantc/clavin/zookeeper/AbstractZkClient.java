package org.iplantc.clavin.zookeeper;

import com.netflix.curator.utils.ZKPaths;

/**
 * An abstract Zookeeper client that implements some common functionality.
 *
 * @author Dennis Roberts
 */
public abstract class AbstractZkClient implements ZkClient {

    /**
     * Validates a Zookeeper path.
     *
     * @param path the path to validate.
     * @return the path that was validated.
     */
    protected String validatePath(String path) {
        if (!path.equals("/") && path.endsWith("/")) {
            throw new IllegalArgumentException("Zookeeper paths may not end with \"/\"");
        }
        return path;
    }

    /**
     * Reads the data at the node corresponding to a base path and a node name.
     *
     * @param base the base path.
     * @param name the node name.
     * @return the data stored in the node as a string.
     */
    public String readNode(String base, String name) {
        validatePath(base);
        return readNode(ZKPaths.makePath(base, name));
    }
}
