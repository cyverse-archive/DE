package org.iplantc.de.server.auth;

import static org.iplantc.de.server.AppLoggerConstants.FWDED_FOR_IP_HEADER_NAME;
import static org.iplantc.de.server.AppLoggerConstants.USER_IP_HEADER_NAME;
import org.iplantc.de.server.AppLoggerUtil;

import com.google.common.base.Strings;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * Performs actions common to most URL connectors.
 */
abstract class BaseUrlConnector implements UrlConnector {

    private AppLoggerUtil appLoggerUtil = AppLoggerUtil.getInstance();

    /**
     * Disables redirects for an HTTP request.
     *
     * @param request the request.
     * @param <T> the type of the request.
     * @return the original request.
     */
    protected <T extends HttpRequestBase> T disableRedirects(T request) {
        HttpClientParams.setRedirecting(request.getParams(), false);
        return request;
    }

    /**
     * Creates a new HTTP GET request.
     *
     * @param url the URL to connect to.
     * @return the request.
     */
    protected HttpGet createHttpGet(String url) {
        return disableRedirects(new HttpGet(url));
    }

    /**
     * Creates a new HTTP PUT request.
     *
     * @param url the URL to connect to.
     * @return the request.
     */
    protected HttpPut createHttpPut(String url) {
        return disableRedirects(new HttpPut(url));
    }

    /**
     * Creates a new HTTP POST request.
     *
     * @param url the URL to connect to.
     * @return the request.
     */
    protected HttpPost createHttpPost(String url) {
        return disableRedirects(new HttpPost(url));
    }

    /**
     * Creates a new HTTP DELETE request.
     *
     * @param url the URL to connect to.
     * @return the request.
     */
    protected HttpDelete createHttpDelete(String url) {
        return disableRedirects(new HttpDelete(url));
    }

    /**
     * Creates a new HTTP PATCH request.
     *
     * @param url the URL to connect to.
     * @return the request.
     */
    protected HttpPatch createHttpPatch(String url) {
        return disableRedirects(new HttpPatch(url));
    }

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
     * Copies headers from the incoming HTTP servlet request to an outgoing
     * HttpRequestBase.
     *
     * Forwards the following headers:
     *   -- User-Agent
     *   -- x-real-ip
     *   -- x-forwarded-for
     *
     * Also creates a unique request id header.
     *
     * @param req the incoming servlet request.
     * @param c the outgoing HttpEntityEnclosingRequestBase.
     * @return the outgoing request.
     */
    protected <T extends HttpRequestBase> T forwardHttpHeaders(HttpServletRequest req, T c) {
        // Forward user-agent header
        c.addHeader("User-Agent", req.getHeader("User-Agent"));

        // Create and add unique request ID header
        T ret = appLoggerUtil.addRequestIdHeader(c);

        // Forward real IP headers
        final String realIp = req.getHeader(USER_IP_HEADER_NAME);
        final String forwardedForIp = req.getHeader(FWDED_FOR_IP_HEADER_NAME);
        if(!Strings.isNullOrEmpty(realIp)){
            ret = appLoggerUtil.addIpHeader(c,
                                            USER_IP_HEADER_NAME,
                                            realIp);
        }
        if(!Strings.isNullOrEmpty(forwardedForIp)){
            ret = appLoggerUtil.addIpHeader(c,
                                            FWDED_FOR_IP_HEADER_NAME,
                                            forwardedForIp);
        }

        return ret;
    }
}
