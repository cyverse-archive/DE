package org.iplantc.de.server.auth;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.io.IOException;

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
    HttpGet getRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP PUT request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    HttpPut putRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP POST request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    HttpPost postRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP DELETE request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    HttpDelete deleteRequest(HttpServletRequest request, String address) throws IOException;

    /**
     * Obtains an HTTP PATCH request object.
     *
     * @param request the servlet request.
     * @param address the address to connect to.
     * @return the request.
     * @throws IOException if the connection can't be established.
     */
    HttpPatch patchRequest(HttpServletRequest request, String address) throws IOException;
}
