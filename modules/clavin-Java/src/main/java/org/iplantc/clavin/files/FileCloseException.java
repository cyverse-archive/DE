package org.iplantc.clavin.files;

import org.iplantc.clavin.ClavinException;

/**
 * Thrown when a properties file can't be closed.
 * 
 * @author Dennis Roberts
 */
public class FileCloseException extends ClavinException {

    /**
     * @param cause the cause of the exception.
     */
    public FileCloseException(Throwable cause) {
        super("the properties file could not be closed", cause);
    }
}
