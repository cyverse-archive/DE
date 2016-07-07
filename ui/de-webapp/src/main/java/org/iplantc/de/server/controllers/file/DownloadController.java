package org.iplantc.de.server.controllers.file;

import static org.iplantc.de.server.AppLoggerConstants.REQUEST_KEY;
import static org.iplantc.de.server.AppLoggerConstants.RESPONSE_KEY;

import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.AppLoggerUtil;
import org.iplantc.de.server.auth.DESecurityConstants;
import org.iplantc.de.server.auth.JwtBuilder;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sriram on 6/28/16.
 */
public class DownloadController {

    final Logger API_REQUEST_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);
    final AppLoggerUtil loggerUtil = AppLoggerUtil.getInstance();
    final AppLoggerUtil appLoggerUtil = loggerUtil;

    @Value("${org.iplantc.services.de-data-mgmt.base}") String dataMgmtServiceBaseUrl;

    @Value("${org.iplantc.services.file-io.base.secured}download") String fileIoBaseUrl;

    @Autowired
    JwtBuilder jwtBuilder;


    /**
     * Process download request
     *
     * @param request
     * @param response
     * @param logRequestUri
     * @param get
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request,
                                HttpServletResponse response,
                                URI logRequestUri,
                                HttpGet get) throws IOException {
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
            final CloseableHttpResponse
                    incomingResponse = loggerUtil.copyRequestIdHeader(get, client.execute(get));
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
    protected void prepareForRequest(HttpGet get, final String logRequestUri) {

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
    protected void sendResponse(HttpServletResponse outgoingResponse, CloseableHttpResponse incomingResponse)
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
}
