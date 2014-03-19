package org.iplantc.de.server;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;

/**
 * Used to establish connections to URLs.
 * 
 * @author Dennis Roberts
 */
public interface UrlConnector {
    /**
     * Obtains a URL connection.
     * 
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the URL connection.
     * @throws IOException if the connection can't be established.
     */
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException;

    /**
     * Obtains an HTTP request base object. We have to use Apache HTTP Client for large multipart POST
     * and PUT requests because the HTTP client code that comes with Java loads the entire request into
     * memory, which tends to cause the JVM to run out of memory for very large files.
     * 
     * @param request the servlet request.
     * @param address the address to connect to.
     * @param method the HTTP request method.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException;
}
