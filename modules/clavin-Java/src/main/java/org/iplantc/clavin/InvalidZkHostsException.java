package org.iplantc.clavin;

/**
 * Thrown when the Zookeeper connection information file is invalid.
 *
 * @author Dennis Roberts
 */
public class InvalidZkHostsException extends ClavinException {

    /**
     * The format string to use when formatting the detail message for this exception.
     */
    private static final String MESSAGE_FORMAT_STR = "Zookeeper connection information file, %s, is invalid";

    /**
     * The path to the Zookeeper connection information file.
     */
    private final String path;

    /**
     * @return the path to the Zookeeper connection information file.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to the Zookeeper connection information file.
     */
    public InvalidZkHostsException(String path) {
        super(String.format(MESSAGE_FORMAT_STR, path));
        this.path = path;
    }
}
