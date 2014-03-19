package org.iplantc.de.server;

import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;

import org.iplantc.de.shared.AuthenticationException;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address)
            throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, (HttpURLConnection) new URL(addIpAddress(address, request)).openConnection());
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address,
            String method) throws IOException {
        validateAuthentication(request);
        return copyUserAgent(request, RequestFactory.buildRequest(method, address));
    }
}
