package org.iplantc.de.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.IOException;

public class HttpException extends IOException implements IsSerializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1713287268088080003L;
    private  int statusCode;
    public int getStatusCode() { return statusCode; }

    private  String responseBody;
    public String getResponseBody() { return responseBody; }

    public HttpException() {
        statusCode = 0;
        responseBody = "";
    }

    public HttpException(final int statusCode, final String responseBody) {
        super(responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
