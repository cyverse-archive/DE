package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when we're unable to read a node from Zookeeper.
 *
 * @author Dennis Roberts
 */
public class ReadNodeException extends ClavinException {

    /**
     * The path to the node.
     */
    private final String path;

    /**
     * @return the path to the node.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to the node.
     * @param cause the cause of this exception.
     */
    public ReadNodeException(String path, Throwable cause) {
        super("unable to read the data at node, " + path, cause);
        this.path = path;
    }
}
