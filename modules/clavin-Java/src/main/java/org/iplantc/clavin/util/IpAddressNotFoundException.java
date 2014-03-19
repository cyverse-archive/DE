package org.iplantc.clavin.util;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when the IP address of the current host can't be determined.
 * 
 * @author Dennis Roberts
 */
public class IpAddressNotFoundException extends ClavinException {

    /**
     * @param cause the cause of this exception.
     */
    public IpAddressNotFoundException(Throwable cause) {
        super("the IP address of the local host could not be determined", cause);
    }
}
