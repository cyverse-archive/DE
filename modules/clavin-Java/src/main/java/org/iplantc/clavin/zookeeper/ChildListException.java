package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when we're unable to list the children of a node in Zookeeper.
 *
 * @author Dennis Roberts
 */
public class ChildListException extends ClavinException {

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
    public ChildListException(String path, Throwable cause) {
        super("unable to list the child nodes of " + path, cause);
        this.path = path;
    }
}
