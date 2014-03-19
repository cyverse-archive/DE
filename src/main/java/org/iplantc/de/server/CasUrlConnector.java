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
 * Used to establish connections to a services that are secured by CAS.  The service must be configured to accept
 * proxy tickets from this server.  The proxy tickets will be sent to the service in the query string parameter,
 * <code>proxyTicket</code>.
 *
 * @author Dennis Roberts
 */
public class CasUrlConnector extends BaseUrlConnector {

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpURLConnection getUrlConnection(HttpServletRequest request, String address) throws IOException {
        return copyUserAgent(request, (HttpURLConnection) addProxyTokenToUrl(address, request).openConnection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpEntityEnclosingRequestBase getRequest(HttpServletRequest request, String address, String method) throws
            IOException {
        String authenticatedUrl = addProxyTokenToUrl(address, request).toString();
        return copyUserAgent(request, RequestFactory.buildRequest(method, authenticatedUrl));
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
            throw new AuthenticationException("unable to obtain a proxy ticket");
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
