package org.iplantc.de.server.auth;

import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;
import org.iplantc.de.shared.exceptions.AuthenticationException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

/**
 * Used to establish connections to a services that are secured by CAS.  The service must be configured to accept
 * proxy tickets from this server.  The proxy tickets will be sent to the service in the query string parameter,
 * <code>proxyTicket</code>.
 *
 * @author Dennis Roberts
 */
public class CasUrlConnector extends BaseUrlConnector {
    private final Logger LOG = LoggerFactory.getLogger(CasUrlConnector.class);

    @Override
    public HttpGet getRequest(HttpServletRequest request, String address) throws IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, createHttpGet(authenticatedUrl));
    }

    @Override
    public HttpPut putRequest(HttpServletRequest request, String address) throws IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, createHttpPut(authenticatedUrl));
    }

    @Override
    public HttpPost postRequest(HttpServletRequest request, String address) throws IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, createHttpPost(authenticatedUrl));
    }

    @Override
    public HttpDelete deleteRequest(HttpServletRequest request, String address) throws IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, createHttpDelete(authenticatedUrl));
    }

    @Override
    public HttpPatch patchRequest(HttpServletRequest request, String address) throws IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, createHttpPatch(authenticatedUrl));
    }

    /**
     * Obtains a CAS proxy ticket and adds it to the URL as a query string parameter.
     *
     * @param address the address used to connect to the service.
     * @param request the incoming HTTP servlet request.
     * @return the updated URL.
     * @throws IOException if an I/O error occurs.
     */
    private URL addProxyTokenToUrl(String address, HttpServletRequest request) throws IOException {
        URL originalUrl = new URL(address);
        address = addIpAddress(address, request);
        address = addQueryParam(address, "proxyToken", getProxyTicket(request, originalUrl));
        return new URL(address);
    }

    /**
     * Gets the proxy ticket for the incoming servlet request and the outgoing address.  The incoming request must
     * have been authenticated by CAS for this to work.
     *
     * @param request the incoming servlet request.
     * @param url     the URL used to contact the service.
     * @return the proxy ticket.
     * @throws IOException if the proxy ticket can't be obtained.
     */
    private String getProxyTicket(HttpServletRequest request, URL url) throws IOException {
        AttributePrincipal principal = attributePrincipalFromServletRequest(request);
        String ticket = principal.getProxyTicketFor(extractServiceName(url));
        if (ticket == null) {
            request.getSession().invalidate();
            final String msg = "unable to obtain a proxy ticket";
            final AuthenticationException authenticationException = new AuthenticationException(msg);
            LOG.error(msg, authenticationException);
            throw authenticationException;
        }
        return ticket;
    }

    /**
     * Extracts service name from the full service address.  The service name consists of the protocol, the hostname
     * and the port if a non-standard port is being used.  For example, http://foo.bar.com:14444, would be the service
     * name for the address, http://foo.bar.com:14444/some/path.  Similarly, https://baz.quux.com would be the service
     * name for the address, https://baz.quux.com/some/path.
     *
     * @param url the URL used to connect to the service.
     * @return the service name.
     */
    private String extractServiceName(URL url) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(url.getProtocol()).append("://");
        builder.append(url.getHost());
        if (url.getPort() >= 0) {
            builder.append(":").append(url.getPort());
        }
        return builder.toString();
    }
}
