package org.iplantc.clavin;

/**
 * Thrown when configuration properties can't be loaded.
 * 
 * @author Dennis Roberts
 */
public class PropertyLoadException extends ClavinException {
    
    /**
     * @param cause the cause of the exception.
     */
    public PropertyLoadException(Throwable cause) {
        super("unable to load the configuration properties", cause);
    }
}
