package org.iplantc.de.server.controllers.file;

import static org.iplantc.de.server.AppLoggerConstants.API_METRICS_LOGGER;
import static org.iplantc.de.server.AppLoggerConstants.REQUEST_KEY;
import static org.iplantc.de.server.AppLoggerConstants.RESPONSE_KEY;

import org.iplantc.de.server.AppLoggerUtil;
import org.iplantc.de.server.auth.DESecurityConstants;
import org.iplantc.de.server.auth.JwtBuilder;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Performs secured file uploads.
 *
 * @author jstroot
 * @author dennis
 */
@Controller
public class SecuredFileUploadController {

    public static final int ENTITY_TOO_LARGE = 413;
    private final Logger API_REQUEST_LOG = LoggerFactory.getLogger(API_METRICS_LOGGER);
    private final AppLoggerUtil loggerUtil = AppLoggerUtil.getInstance();

    @Autowired private JwtBuilder jwtBuilder;

    @Value("${org.iplantc.services.file-io.secured.file-upload}") String securedFileUploadUrl;

    @RequestMapping(value = "/de/secured/fileUpload", method = RequestMethod.POST)
    public ResponseEntity<Object> doSecureFileUpload(@RequestParam("dest") final String dest,
                                                     @RequestParam("file") final MultipartFile file,
                                                     HttpServletRequest request)
            throws IOException, URISyntaxException, ServletException {

        // Prepare to process the request.
        final URI logRequestUri = buildUri(dest);

        // Create the request.
        final URI uri = buildUri(dest);
        final HttpPost post = new HttpPost(uri);
        try {
            post.setHeader(DESecurityConstants.JWT_CUSTOM_HEADER, jwtBuilder.buildJwt(request));
            post.setEntity(buildMultipartEntity(file));
            prepareForRequest(post, logRequestUri.toString());
        } catch (JoseException e) {
            API_REQUEST_LOG.error("GET " + logRequestUri.toString(), e);
            MDC.remove(RESPONSE_KEY);
            throw new IOException("unable to generate JWT", e);
        }

        // Send the request.
        CloseableHttpClient client = HttpClients.createDefault();
        ResponseEntity<Object> response = null;
        try {
            final long requestStartTime = System.currentTimeMillis();
            final CloseableHttpResponse incomingResponse = loggerUtil.copyRequestIdHeader(post, client.execute(post));
            if(incomingResponse.getStatusLine().getStatusCode() == ENTITY_TOO_LARGE) {
                throw new Exception("File too large to upload!");
            }
            final long responseRecvTime = System.currentTimeMillis();
            final String responseJson = loggerUtil.createMdcResponseMapJson(incomingResponse,
                                                                            BaseServiceCallWrapper.Type.GET,
                                                                            logRequestUri.toString(),
                                                                            null,
                                                                            responseRecvTime - requestStartTime);
            MDC.put(RESPONSE_KEY, responseJson);
            API_REQUEST_LOG.info("POST {}", logRequestUri.toString());
            response = formatResponse(incomingResponse);
        } catch(Exception e) {
            API_REQUEST_LOG.error("POST " + logRequestUri.toString(), e);
        } finally {
            MDC.remove(RESPONSE_KEY);
            client.close();
        }

        return response;
    }

    /**
     * Prepares for request processing. The request URI needs to be reassembled and stored in the
     * Mapped Diagnostic Context (MDC). The URI itself also needs to be placed in the API request
     * log.
     *
     * @param post the request
     * @param logRequestUri the URI to use when forwarding the request.
     */
    private void prepareForRequest(HttpPost post, final String logRequestUri) {

        try {
            HttpPost reqWithHeader = loggerUtil.addRequestIdHeader(post);
            String request = loggerUtil.createMdcRequestMapAsJsonString(reqWithHeader);
            MDC.put(REQUEST_KEY, request);
            API_REQUEST_LOG.info("POST {}", logRequestUri);
        } catch (Exception e) {
            API_REQUEST_LOG.error("POST " + logRequestUri, e);
        } finally {
            MDC.remove(REQUEST_KEY);
        }

    }

    /**
     * Builds the request URI containing a destination and optionally, a proxy token. The proxy token
     * is used in the actual request. The destination is used in log messages and in the request.
     *
     * @param dest the destination path to place in the query string.
     * @return the URI.
     * @throws URISyntaxException if the base URI {@code securedFileUploadUrl} is malformed.
     */
    private URI buildUri(final String dest) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(securedFileUploadUrl).setParameter("dest", dest);
        return uriBuilder.build();
    }

    /**
     * Builds the entity for the forwarded request.
     *
     * @param file the incoming multipart file.
     * @return the outgoing request entity.
     * @throws IOException if an I/O error occurs.
     */
    private HttpEntity buildMultipartEntity(MultipartFile file) throws IOException {
        final String name = file.getName();
        final InputStream inputStream = file.getInputStream();
        final ContentType contentType = ContentType.parse(file.getContentType());
        final String fileName = file.getOriginalFilename();
        return MultipartEntityBuilder.create()
                .addBinaryBody(name, inputStream, contentType, fileName)
                .build();
    }

    /**
     * Formats the response to send back to the client. The response body is also added to the
     * Mapped Diagnostic Context (MDC) after it is read.
     *
     * @param response the response from the upload service.
     * @return the response to send back.
     * @throws IOException if an I/O error occurs.
     */
    private ResponseEntity<Object> formatResponse(CloseableHttpResponse response) throws IOException {
        try {
            final String body = IOUtils.toString(response.getEntity().getContent());
            return new ResponseEntity<Object>(body, HttpStatus.OK);
        } finally {
            response.close();
        }
    }
}
