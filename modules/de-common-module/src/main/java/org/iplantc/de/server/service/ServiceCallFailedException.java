package org.iplantc.de.server.service;

/**
 * Thrown when a service call fails.
 *
 * @author Dennis Roberts
 */
public class ServiceCallFailedException extends RuntimeException {
    private static final long serialVersionUID = 8933998477241299961L;

    /**
     * The default constructor.
     */
    public ServiceCallFailedException() {
        super();
    }

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

    /**
     * @param msg the detail message.
     * @param cause the cause of this exception.
     */
    public ServiceCallFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
