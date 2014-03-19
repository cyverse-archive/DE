package org.iplantc.clavin.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility methods used to obtain information about the local host.
 *
 * @author Dennis Roberts
 */
public class HostUtils {

    /**
     * Prevent instantiation.
     */
    private HostUtils() {}

    /**
     * @return the IP address for the local host as a string.
     * @throws IpAddressNotFoundException if the IP address of the local host can't be found.
     */
    public static String getIpAddress() throws IpAddressNotFoundException {
        try {
           return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            throw new IpAddressNotFoundException(e);
        }
    }
}
