package org.iplantc.de.server;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * Performs actions common to most URL connectors.
 */
public abstract class BaseUrlConnector implements UrlConnector {
    
    /**
     * Adds a query string parameter to a URI.
     *
     * @param uriString the string representation URI to update.
     * @param name the name of query string parameter.
     * @param value the value of the query string parameter.
     * @return the string representation of the updated URI.
     * @throws IOException if a URI representation is invalid or an encoding error occurs.
     */
    protected String addQueryParam(String uriString, String name, String value) throws IOException {
        try {
            return new URIBuilder(uriString).addParameter(name, value).build().toString();
        }
        catch (URISyntaxException e) {
            String msg = "unable to add query string parameter " + name + "="
                    + URLEncoder.encode(value, "UTF-8");
            throw new IOException(msg, e);
        }
    }

    /**
     * Adds the remote IP address to the query string of a URI.
     *
     * @param uriString the string representation of the URI to update.
     * @param request the HTTP servlet request object.
     * @return the string representation of the updated URI.
     * @throws URISyntaxException if there's a syntax error in the updated URI.
     * @throws IOException if a URI representation is invalid or an encoding error occurs.
     */
    protected String addIpAddress(String uriString, HttpServletRequest request) throws IOException {
        return addQueryParam(uriString, "ip-address", request.getRemoteAddr());
    }

    /**
     * Copies the User-Agent header from the incoming HTTP servlet request to an outgoing
     * HttpRequestBase.
     * 
     * @param req the incoming servlet request.
     * @param c the outgoing HttpEntityEnclosingRequestBase.
     * @return the outgoing request.
     */
    protected <T extends HttpRequestBase> T copyUserAgent(HttpServletRequest req, T c) {
        c.addHeader("User-Agent", req.getHeader("User-Agent"));
        return c;
    }
}
