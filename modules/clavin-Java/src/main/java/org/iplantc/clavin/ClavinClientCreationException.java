package org.iplantc.clavin;

/**
 * Thrown when a Clavin client could not be created.
 *
 * @author Dennis Roberts
 */
public class ClavinClientCreationException extends ClavinException {

    /**
     * The format string to use when generating the detail message.
     */
    private static final String MESSAGE_FORMAT_STR
            = "No configuration file was found for %s and the Zookeeper connection information file, %s, "
            + "was not found.";

    /**
     * The name of the service.
     */
    private String serviceName;

    /**
     * The path to the file containing the Zookeeper connection information.
     */
    private String zkHostsPath;

    /**
     * @return the name of the service.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the path to the file containing the Zookeeper connection information.
     */
    public String getZkHostsPath() {
        return zkHostsPath;
    }

    /**
     * @param serviceName the name of the service.
     * @param zkHostsPath the path to the file containing the Zookeeper connection information.
     */
    public ClavinClientCreationException(String serviceName, String zkHostsPath) {
        super(String.format(MESSAGE_FORMAT_STR, serviceName, zkHostsPath));
        this.serviceName = serviceName;
        this.zkHostsPath = zkHostsPath;
    }
}
