package org.iplantc.de.shared.exceptions;

/**
 * Thrown when a services call fails.
 *
 * @author Dennis Roberts
 */
public class ServiceCallFailedException extends RuntimeException {
    private static final long serialVersionUID = 8933998477241299961L;

    /**
     * @param msg the detail message.
     */
    public ServiceCallFailedException(String msg) {
        super(msg);
    }

    /**
     * @param cause the cause of this exception.
     */
    public ServiceCallFailedException(Throwable cause) {
        super(cause);
    }

}
