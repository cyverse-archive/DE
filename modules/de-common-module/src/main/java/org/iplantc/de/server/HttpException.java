package org.iplantc.de.server;

import java.io.IOException;

public class HttpException extends IOException {

    private final int statusCode;
    public int getStatusCode() { return statusCode; }

    private final String responseBody;
    public String getResponseBody() { return responseBody; }

    public HttpException(int statusCode, String responseBody) {
        super("the server returned status code " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
