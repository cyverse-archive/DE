package org.iplantc.de.shared;

import java.io.IOException;

/**
 * Thrown by the service dispatcher when an HTTP redirect is received.
 */
public class HttpRedirectException extends HttpException {

    private String location;
    public String getLocation() { return location; }

    public HttpRedirectException() {
        super();
        location = "";
    }

    public HttpRedirectException(int statusCode, String responseBody, String location) {
        super(statusCode, responseBody);
        this.location = location;
    }
}
