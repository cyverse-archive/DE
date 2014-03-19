package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinException;


/**
 * Thrown when a node doesn't exist in Zookeeper.
 * 
 * @author Dennis Roberts
 */
public class NoNodeException extends ClavinException {

    /**
     * The path to the node.
     */
    private String path;

    /**
     * @return the path to the node.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to the node.
     */
    public NoNodeException(String path) {
        super("no node found for " + path);
        this.path = path;
    }
}
