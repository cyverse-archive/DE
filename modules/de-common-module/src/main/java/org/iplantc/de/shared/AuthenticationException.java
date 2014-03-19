package org.iplantc.de.shared;

import java.io.IOException;

/**
 * Thrown by the service dispatcher classes when it appears that a user is not authenticated.
 */
public class AuthenticationException extends IOException {

    /**
     * Default constructor.
     */
    public AuthenticationException() {
        super();
    }

    /**
     * @param msg the exception detail message.
     */
    public AuthenticationException(String msg) {
        super(msg);
    }

    /**
     * @param cause the cause of this exception.
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param msg the exception detail message.
     * @param cause the cause of this exception.
     */
    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
