package org.iplantc.de.server;

import org.apache.http.client.methods.*;

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
     * Obtains an HTTP GET request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpGet getRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP PUT request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpPut putRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP POST request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpPost postRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP DELETE request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpDelete deleteRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP PATCH request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    public HttpPatch patchRequest(HttpServletRequest request, String address) throws IOException;
}
