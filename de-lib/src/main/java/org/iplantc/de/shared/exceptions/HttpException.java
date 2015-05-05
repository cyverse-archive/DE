package org.iplantc.de.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.IOException;

public class HttpException extends IOException implements IsSerializable{

    private int statusCode;
    public int getStatusCode() { return statusCode; }

    private String responseBody;
    public String getResponseBody() { return responseBody; }

    public HttpException() {
        statusCode = 0;
        responseBody = "";
    }

    public HttpException(int statusCode, String responseBody) {
        super("the server returned status code " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
