package org.iplantc.de.server.controllers.file;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.AppLoggerUtil;
import org.iplantc.de.server.auth.DESecurityConstants;
import org.iplantc.de.server.auth.JwtBuilder;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.iplantc.de.server.AppLoggerConstants.REQUEST_KEY;
import static org.iplantc.de.server.AppLoggerConstants.RESPONSE_KEY;

/**
 * Processes simple file download requests.
 *
 * @author jstroot
 */
@Controller
public class FileDownloadController {

    private final Logger API_REQUEST_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);
    private final AppLoggerUtil loggerUtil = AppLoggerUtil.getInstance();
    private final AppLoggerUtil appLoggerUtil = loggerUtil;

    @Value("${org.iplantc.services.de-data-mgmt.base}") String dataMgmtServiceBaseUrl;

    @Value("${org.iplantc.services.file-io.base.secured}download") String fileIoBaseUrl;

    @Autowired private JwtBuilder jwtBuilder;

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

        // Create the request.
        final URI uri = buildUri(url, path);
        final HttpGet get = new HttpGet(uri);
        try {
            get.setHeader(DESecurityConstants.JWT_CUSTOM_HEADER, jwtBuilder.buildJwt(request));
            get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            prepareForRequest(get, logRequestUri.toString());
        } catch (JoseException e) {
            API_REQUEST_LOG.error("GET " + logRequestUri.toString(), e);
            MDC.remove(RESPONSE_KEY);
            throw new IOException("unable to generate JWT", e);
        }

        // Send the request.
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            final long requestStartTime = System.currentTimeMillis();
            final CloseableHttpResponse incomingResponse = loggerUtil.copyRequestIdHeader(get, client.execute(get));
            final long responseRecvTime = System.currentTimeMillis();
            final String responseJson = appLoggerUtil.createMdcResponseMapJson(incomingResponse,
                                                                               BaseServiceCallWrapper.Type.GET,
                                                                               logRequestUri.toString(),
                                                                               null,
                                                                               responseRecvTime - requestStartTime);
            MDC.put(RESPONSE_KEY, responseJson);
            API_REQUEST_LOG.info("GET {}", logRequestUri.toString());
            sendResponse(response, incomingResponse);
        } catch (Exception e) {
            API_REQUEST_LOG.error("GET " + logRequestUri.toString(), e);
            throw e;
        } finally {
            MDC.remove(RESPONSE_KEY);
            client.close();
        }
    }

    /**
     * Prepares for request processing. The request URI needs to be reassembled and stored in the
     * Mapped Diagnostic Context (MDC). The URI itself also needs to be placed in the API request
     * log.
     *
     * @param get the request
     * @param logRequestUri the URI to use when forwarding the request.
     */
    private void prepareForRequest(HttpGet get, final String logRequestUri) {

        try {
            HttpGet reqWithIdHeader = loggerUtil.addRequestIdHeader(get);
            String request = appLoggerUtil.createMdcRequestMapAsJsonString(reqWithIdHeader);
            MDC.put(REQUEST_KEY, request);
            API_REQUEST_LOG.info("GET {}", logRequestUri);
        } catch (Exception e) {
            API_REQUEST_LOG.error("GET " + logRequestUri, e);
        } finally {
            MDC.remove(REQUEST_KEY);

        }
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
     * @return the URI.
     * @throws URISyntaxException if the base URI {@code fileIoBaseUrl} or
     *         {@code dataMgmtServiceBaseUrl} is malformed.
     */
    private URI buildUri(final String url, final String path) throws URISyntaxException {

        final String baseUrl = StringUtils.isEmpty(url) ? fileIoBaseUrl : dataMgmtServiceBaseUrl + url;
        final URIBuilder uriBuilder = new URIBuilder(baseUrl).setParameter("path", path);

        return uriBuilder.build();
    }
}
