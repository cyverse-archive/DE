package org.iplantc.de.server.auth;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.iplantc.de.server.AppLoggerConstants;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jose4j.jwt.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;

/**
 * Used to establish connections to a services that are secured using JSON Web Tokens. The service must
 * be configured to accept JWTs in the {@code X-Iplant-De-Jwt} custom header.
 *
 * @author dennis
 */
public class JwtUrlConnector extends BaseUrlConnector implements UrlConnector {
    private final Logger API_METRICS_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);

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
    private <T extends HttpRequestBase> T addHeaders(final HttpServletRequest request, final T c) {
        c.addHeader("X-Iplant-De-Jwt", buildJwt(request));
        return copyUserAgent(request, c);
    }

    /**
     * Builds a JWT from the incoming HTTP servlet request.
     *
     * @param request the incoming servlet request.
     * @return a signed jwt.
     */
    private String buildJwt(final HttpServletRequest request) {
        final AttributePrincipal principal = attributePrincipalFromServletRequest(request);

        // Extract the user's first and last name from the attributes.
        final String firstName = getStringAttribute(principal, "firstName");
        final String lastName = getStringAttribute(principal, "lastName");

        // Build the JWT claims.
        JwtClaims claims = new JwtClaims();
        claims.setSubject(principal.getName());
        claims.setClaim("email", getStringAttribute(principal, "email"));
        claims.setClaim("given_name", firstName);
        claims.setClaim("family_name", lastName);
        claims.setClaim("name", firstName + " " + lastName);
        claims.setClaim("entitlement", extractGroups(principal));

        return null;
    }

    /**
     * Obtains the value of a user attribute as a string. Returns null if the attribute is missing
     * or null.
     *
     * @param principal the attribute principal from the incoming request.
     * @param attribute the name of the attribute to extract.
     * @return the attribute as a string or null.
     */
    private String getStringAttribute(final AttributePrincipal principal, final String attribute) {
        final Object value = principal.getAttributes().get(attribute);
        return value == null ? null : value.toString();
    }

    /**
     * Extracts the groups from the user's {@code entitlement} attribute.
     *
     * @param principal the attribute principal from the incoming request.
     * @return the list of groups.
     */
    private String[] extractGroups(final AttributePrincipal principal) {
        final String groupStr = getStringAttribute(principal, "entitlement");
        if (groupStr == null || groupStr.matches("^\\s*$")) {
            return null;
        }

        // Remove the leading and trailing square brackets then split the string.
        return groupStr.replaceAll("^\\s*\\[|\\]\\s*$", "").split(",\\s*");
    }
}
