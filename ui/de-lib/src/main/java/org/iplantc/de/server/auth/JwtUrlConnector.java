package org.iplantc.de.server.auth;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.iplantc.de.server.AppLoggerConstants;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Used to establish connections to a services that are secured using JSON Web Tokens. The service must
 * be configured to accept JWTs in the {@code X-Iplant-De-Jwt} custom header.
 *
 * @author dennis
 */
@Component
public class JwtUrlConnector extends BaseUrlConnector implements UrlConnector {
    private final Logger API_METRICS_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);

    @Autowired private JwtBuilder jwtBuilder;

    @Override
    public HttpGet getRequest(HttpServletRequest request, String address) throws IOException {
        return addHeaders(request, createHttpGet(addIpAddress(address, request)));
    }

    @Override
    public HttpPut putRequest(HttpServletRequest request, String address) throws IOException {
        return addHeaders(request, createHttpPut(addIpAddress(address, request)));
    }

    @Override
    public HttpPost postRequest(HttpServletRequest request, String address) throws IOException {
        return addHeaders(request, createHttpPost(addIpAddress(address, request)));
    }

    @Override
    public HttpDelete deleteRequest(HttpServletRequest request, String address) throws IOException {
        return addHeaders(request, createHttpDelete(addIpAddress(address, request)));
    }

    @Override
    public HttpPatch patchRequest(HttpServletRequest request, String address) throws IOException {
        return addHeaders(request, createHttpPatch(addIpAddress(address, request)));
    }

    /**
     * Adds required headers to the outgoing request.
     *
     * @param request the incoming servlet request.
     * @param c the outgoing HttpRequestBase.
     * @return the outgoing request.
     */
    private <T extends HttpRequestBase> T addHeaders(final HttpServletRequest request, final T c)
            throws IOException {

        // Build the JWT.
        String jwt;
        try {
            jwt = jwtBuilder.buildJwt(request);
        } catch (JoseException e) {
            throw new IOException("Unable to build and sign JWT", e);
        }

        // Update the message headers.
        c.addHeader(DESecurityConstants.JWT_CUSTOM_HEADER, jwt);
        return forwardHttpHeaders(request, c);
    }
}
