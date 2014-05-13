package org.iplantc.de.server;

import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;

import org.apache.http.client.methods.*;
import org.iplantc.de.shared.AuthenticationException;

import org.jasig.cas.client.authentication.AttributePrincipal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

/**
 * A URL connector that verifies that the user has authenticated, but does not add authentication
 * information to the outgoing request.
 * 
 * @author Dennis Roberts
 */
public class AuthenticationValidatingUrlConnector extends BaseUrlConnector {

    /**
     * Verifies that the user is authenticated.
     * 
     * @throws IOException if the user is not authenticated.
     */
    private void validateAuthentication(HttpServletRequest request) throws IOException {
        AttributePrincipal principal = attributePrincipalFromServletRequest(request);
        if (principal == null) {
            throw new AuthenticationException();
        }
    }

    @Override
    public HttpGet getRequest(HttpServletRequest request, String address) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, new HttpGet(address));
    }

    @Override
    public HttpPut putRequest(HttpServletRequest request, String address) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, new HttpPut(address));
    }

    @Override
    public HttpPost postRequest(HttpServletRequest request, String address) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, new HttpPost(address));
    }

    @Override
    public HttpDelete deleteRequest(HttpServletRequest request, String address) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, new HttpDelete(address));
    }

    @Override
    public HttpPatch patchRequest(HttpServletRequest request, String address) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, new HttpPatch(address));
    }
}
