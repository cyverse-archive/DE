package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when we're unable to connect to Zookeeper.
 *
 * @author Dennis Roberts
 */
public class ZookeeperConnectionException extends ClavinException {

    /**
     * The connection specification string.
     */
    private final String connectionSpec;

    /**
     * @return the connection specification string.
     */
    public String getConnectionSpec() {
        return connectionSpec;
    }

    /**
     * @param connectionSpec the connection specification string.
     * @param cause the cause of this exception.
     */
    public ZookeeperConnectionException(String connectionSpec, Throwable cause) {
        super("unable to connect to Zookeeper using connection spec, " + connectionSpec, cause);
        this.connectionSpec = connectionSpec;
    }
}
