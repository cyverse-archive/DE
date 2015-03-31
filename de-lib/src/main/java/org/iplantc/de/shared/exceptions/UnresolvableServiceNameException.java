package org.iplantc.de.shared.exceptions;

/**
 * Indicates that a service name couldn't be resolved.
 *
 * @author Dennis Roberts
 */
public class UnresolvableServiceNameException extends RuntimeException {
    private static final long serialVersionUID = -4682556048970360074L;
    /**
     * The service name that couldn't be resolved.
     */
    private final String serviceName;

    /**
     * @param serviceName the service name that couldn't be resolved.
     */
    public UnresolvableServiceNameException(String serviceName) {
        super("unable to resolve service name: " + serviceName);
        this.serviceName = serviceName;
    }

    /**
     * @return the service name that couldn't be resolved.
     */
    public String getServiceName() {
        return serviceName;
    }
}
