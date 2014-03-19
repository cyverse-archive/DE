package org.iplantc.clavin;

/**
 * The root exception class for clavin-java.
 * 
 * @author Dennis Roberts
 */
public class ClavinException extends RuntimeException {

    /**
     * The default constructor.
     */
    public ClavinException() {
        super();
    }

    /**
     * @param msg a brief message describing the error.
     */
    public ClavinException(String msg) {
        super(msg);
    }

    /**
     * @param cause the cause of the error.
     */
    public ClavinException(Throwable cause) {
        super(cause);
    }

    /**
     * @param msg a brief message describing the error.
     * @param cause the cause of the error.
     */
    public ClavinException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
