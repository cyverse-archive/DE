package org.iplantc.clavin.zookeeper;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when a Zookeeper operation is attempted and there is no connection to Zookeeper.
 *
 * @author Dennis Roberts
 */
public class ZookeeperNotConnectedException extends ClavinException {

    /**
     * Default constructor.
     */
    public ZookeeperNotConnectedException() {
        super("please call ZkClient.connect before attempting to access Zookeeper");
    }
}
