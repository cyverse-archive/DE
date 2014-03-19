package org.iplantc.clavin;

/**
 * Thrown when a service is not permitted to run on the current host.
 * 
 * @author Dennis Roberts
 */
public class ServiceNotPermittedException extends ClavinException {

    /**
     * The name of the service.
     */
    private String service;

    /**
     * The IP address of the host.
     */
    private String host;

    /**
     * @param service the name of the service.
     * @param host the name of the host.
     */
    public ServiceNotPermittedException(String service, String host) {
        super("the service, " + service + ", is not permitted to run on host, " + host);
        this.service = service;
        this.host = host;
    }

    /**
     * @return the name of the service.
     */
    public String getService() {
        return service;
    }

    /**
     * @return the IP address of the host.
     */
    public String getHost() {
        return host;
    }
}
