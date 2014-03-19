package org.iplantc.clavin.zookeeper;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;

import org.apache.log4j.Logger;

/**
 *
 * @author dennis
 */
public class LoggingConnectionStateListener implements ConnectionStateListener {

    /**
     * Used to log the connection state.
     */
    private static final Logger LOG = Logger.getLogger(LoggingConnectionStateListener.class);

    /**
     * Logs the new connection state.
     *
     * @param cf the curator framework.
     * @param cs the connection state.
     */
    public void stateChanged(CuratorFramework cf, ConnectionState cs) {
        LOG.info("Connection State: " + cs);
    }
}
