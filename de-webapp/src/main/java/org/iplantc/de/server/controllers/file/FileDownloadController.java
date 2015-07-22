package org.iplantc.de.server.controllers.file;

import static org.iplantc.de.server.AppLoggerConstants.REQUEST_KEY;
import static org.iplantc.de.server.AppLoggerConstants.REQUEST_METHOD_KEY;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.util.CasUtils;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Processes simple file download requests.
 *
 * @author jstroot
 */
@Controller
public class FileDownloadController {

    private final Logger API_REQUEST_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);

    @Value("${org.iplantc.services.de-data-mgmt.base}") String dataMgmtServiceBaseUrl;

    @Value("${org.iplantc.services.file-io.base.secured}download") String fileIoBaseUrl;

    @RequestMapping(value = "/de/secured/fileDownload", method = RequestMethod.GET)
    public void doSecureFileDownload(@RequestParam("path") final String path,
                                     @RequestParam(value = "attachment",
                                                   required = false,
                                                   defaultValue = "1") final String attachment,
                                     @RequestParam(value = "url",
                                                   required = false,
                                                   defaultValue = "") final String url,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response)
            throws IOException, URISyntaxException {

        // Prepare to process the request.
        final URI logRequestUri = buildUri(url, path);
        prepareForRequest(logRequestUri.toString());

        // Create the request.
        final URI uri = buildUri(url, path, getProxyToken(request, extractServiceName(logRequestUri)));
        final HttpGet get = new HttpGet(uri);
        get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // Send the request.
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            sendResponse(response, client.execute(get));
        } finally {
            client.close();
        }

        // Clean up.
        cleanUpAfterRequest();
    }

    /**
     * Prepares for request processing. The request URI needs to be reassembled and stored in the
     * Mapped Diagnostic Context (MDC). The URI itself also needs to be placed in the API request
     * log.
     *
     * @param logRequestUri the URI to use when forwarding the request.
     */
    private void prepareForRequest(final String logRequestUri) {

        // Add the URL to the MDC.
        MDC.put(REQUEST_KEY, logRequestUri);
        MDC.put(REQUEST_METHOD_KEY, "GET");

        // Log the request.
        API_REQUEST_LOG.info("GET {}", logRequestUri);
    }

    /**
     * Cleans up the Mapped Diagnostic Context (MDC) after a request has been processed.
     */
    private void cleanUpAfterRequest() {
        MDC.remove(REQUEST_KEY);
        MDC.remove(REQUEST_METHOD_KEY);
    }

    /**
     * Streams the incoming response from the file download service back to the client.
     *
     * @param outgoingResponse the response back to the client.
     * @param incomingResponse the response from the file download service.
     * @throws IOException if an I/O error occurs.
     */
    private void sendResponse(HttpServletResponse outgoingResponse, CloseableHttpResponse incomingResponse)
        throws IOException {

        HttpEntity responseEntity = incomingResponse.getEntity();

        // Prepare the outgoing response.
        outgoingResponse.setStatus(incomingResponse.getStatusLine().getStatusCode());
        outgoingResponse.setContentType(responseEntity.getContentType().getValue());
        outgoingResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                incomingResponse.getFirstHeader(HttpHeaders.CONTENT_DISPOSITION).getValue());
        outgoingResponse.setContentLength(new Long(responseEntity.getContentLength()).intValue());

        // Copy the response entity.
        try {
            responseEntity.writeTo(outgoingResponse.getOutputStream());
        } finally {
            incomingResponse.close();
        }
    }

    /**
     * Builds the URI for the forwarded request.
     *
     * @param url the relative URL for the data management service. Omitted if empty.
     * @param path the path to place in the query string.
     * @param proxyToken the proxy token to place in the query string. Omitted if null.
     * @return the URI.
     * @throws URISyntaxException if the base URI {@code fileIoBaseUrl} or
     *         {@code dataMgmtServiceBaseUrl} is malformed.
     */
    private URI buildUri(final String url, final String path, final String proxyToken)
            throws URISyntaxException {

        final String baseUrl = StringUtils.isEmpty(url) ? fileIoBaseUrl : dataMgmtServiceBaseUrl + url;
        final URIBuilder uriBuilder = new URIBuilder(baseUrl).setParameter("path", path);

        // Add the proxy token if it's present.
        if (proxyToken != null) {
            uriBuilder.setParameter("proxyToken", proxyToken);
        }

        return uriBuilder.build();
    }

    /**
     * Builds the URI for the forwarded request.
     *
     * @param url the relative URL for the data management service. Omitted if empty.
     * @param path the path to place in the query string.
     * @return the URI.
     * @throws URISyntaxException if the base URI {@code fileIoBaseUrl} or
     *         {@code dataMgmtServiceBaseUrl} is malformed.
     */
    private URI buildUri(final String url, final String path) throws URISyntaxException {
        return buildUri(url, path, null);
    }

    /**
     * Extracts the CAS service name from a request URI.
     *
     * @param uri the request URI.
     * @return the CAS service name.
     */
    private String extractServiceName(URI uri) {
        return new StringBuilder()
                .append(uri.getScheme())
                .append("://")
                .append(uri.getAuthority())
                .toString();
    }

    /**
     * Obtains a CAS proxy token for the authenticated user.
     *
     * @param request the incoming servlet request.
     * @param serviceName the service name to use for the CAS proxy token.
     * @return the proxy token.
     */
    private String getProxyToken(final HttpServletRequest request, final String serviceName) {
        return CasUtils.attributePrincipalFromServletRequest(request).getProxyTicketFor(serviceName);
    }
}
