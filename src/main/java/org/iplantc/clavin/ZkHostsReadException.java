package org.iplantc.clavin;

/**
 * Thrown when the file containing the Zookeeper connection information can't be read.
 *
 * @author Dennis Roberts
 */
public class ZkHostsReadException extends ClavinException {

    /**
     * The format string to use when creating the detail message for this exception.
     */
    private static final String MESSAGE_FORMAT_STR = "unable to retrieve the Zookeeper connection information from %s";

    /**
     * The path to the Zookeeper connection information file.
     */
    private String path;

    /**
     * @return the path to the Zookeeper connection information file.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to the Zookeeper connection information file.
     * @param cause the cause of this exception.
     */
    public ZkHostsReadException(String path, Throwable cause) {
        super(String.format(MESSAGE_FORMAT_STR, path), cause);
        this.path = path;
    }
}
