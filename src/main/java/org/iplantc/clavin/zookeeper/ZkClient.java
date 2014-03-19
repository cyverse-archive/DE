package org.iplantc.clavin.zookeeper;

/**
 * A simple Zookeeper client interface that allows us to mock Zookeeper connections for unit testing.
 *
 * @author Dennis Roberts
 */
public interface ZkClient {

    /**
     * Establishes the connection to Zookeeper.
     */
    public void connect();

    /**
     * Drops the connection to Zookeeper.
     */
    public void disconnect();

    /**
     * Gets the list of children for the node corresponding to a path.
     *
     * @param path the path to the node.
     * @return the list of children.
     */
    public Iterable<String> getChildren(String path);

    /**
     * Reads the data at the node corresponding to a path.
     *
     * @param path the path to the node.
     * @return the data stored in the node as a string.
     */
    public String readNode(String path);

    /**
     * Reads the data at the node corresponding to a base path and a node name.
     *
     * @param base the base path.
     * @param name the node name.
     * @return the data stored in the node as a string.
     */
    public String readNode(String base, String name);
}
