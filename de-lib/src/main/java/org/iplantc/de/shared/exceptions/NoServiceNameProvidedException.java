package org.iplantc.de.shared.exceptions;

/**
 * Thrown by the proxy servlet when no services name was provided by the caller.
 *
 * @author Dennis Roberts
 */
public class NoServiceNameProvidedException extends RuntimeException {

    private static final long serialVersionUID = -2737020121564493519L;

    /**
     * The default constructor.
     */
    public NoServiceNameProvidedException() {
        super("no services name was provided");
    }
}
