package org.iplantc.de.server.controllers;

import org.iplantc.de.server.util.CasUtils;

import com.google.common.collect.Sets;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A secured servlet that forwards requests directly to other services.
 *
 * @author Dennis Roberts
 * @author jstroot
 */
@Controller
public class ProxyController {

    @Value("${org.iplantc.services.file-io.secured.file-upload}") String securedFileUploadUrl;
    @Value("${org.iplantc.services.file-io.file-upload}") String unsecuredFileUploadUrl;
    /**
     * The set of headers that should be skipped when copying headers.
     */
    private static final Set<String> HEADERS_TO_SKIP = Sets.newHashSet("content-length");
    private final Logger LOG = LoggerFactory.getLogger(ProxyController.class);

    /**
     * Forwards an HTTP POST request to the secured file upload service.
     */
//    @RequestMapping(value = "/de/secured/fileUpload", method = RequestMethod.POST)
    public void doSecuredUpload(final HttpServletRequest request,
                                final HttpServletResponse response,
                                @RequestParam("dest") String dest,
                                @RequestParam("user") String user,
                                @RequestParam("file") MultipartFile file) throws IOException {

        try {
            final String uploadUrl = buildSecureUploadUrl(securedFileUploadUrl, dest, request);
            LOG.debug("Secured file upload url = {}", uploadUrl);
            forwardRequest(new HttpPost(uploadUrl), request, response);
        } catch (IOException | URISyntaxException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    @RequestMapping(value = "/de/fileUpload", method = RequestMethod.POST)
    public void doUnsecuredUpload(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  @RequestParam("dest") String dest,
                                  @RequestParam("user") String user,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        final String uploadUrl = unsecuredFileUploadUrl;
        LOG.debug("Unsecured file Upload url = {}", uploadUrl);
        try {
            forwardRequest(new HttpPost(uploadUrl), request, response);
        } catch (IOException e) {
            sendErrorResponse(response, e.getMessage());
        }
    }

    /**
     * Forwards a request that can contain a request bod (for example, a POST or PUT request).
     *
     * @param out the outgoing request entity.
     * @param request the servlet request.
     * @param response the servlet response.
     * @throws IOException if an I/O exception occurs.
     */
    protected void forwardRequest(final HttpEntityEnclosingRequestBase out,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) throws IOException {
        HttpClient client = new DefaultHttpClient();
//        HttpClient client1 = HttpClientBuilder.create().build();
        try {

            copyHeaders(request, out);
            out.setEntity(new InputStreamEntity(request.getInputStream(), request.getContentLength()));
            copyResponse(client.execute(out), response);
        } finally {
            out.releaseConnection();
        }
    }

    private String buildSecureUploadUrl(final String securedFileUploadUrl,
                                        final String dest,
                                        final HttpServletRequest request) throws URISyntaxException, IOException {
        AttributePrincipal principal = CasUtils.attributePrincipalFromServletRequest(request);
        String proxyToken = principal.getProxyTicketFor(extractServiceName(new URL(securedFileUploadUrl)));

        return new URIBuilder(securedFileUploadUrl)
                               .addParameter("ip-address", request.getRemoteAddr())
                               .addParameter("proxyToken", proxyToken)
                               .addParameter("dest", dest)
                               .build().toString();

    }

    /**
     * Copies all request headers from an incoming servlet request to an outgoing request.
     *
     * @param source the incoming request.
     * @param dest   the outgoing request.
     */
    private void copyHeaders(HttpServletRequest source, HttpRequestBase dest) {
        Enumeration<String> names = source.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (!HEADERS_TO_SKIP.contains(name.toLowerCase())) {
                Enumeration<String> values = source.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    dest.addHeader(name, value);
                }
            }
        }
    }

    /**
     * Copies all response headers from an incoming response to an outgoing servlet response.
     *
     * @param source the incoming response.
     * @param dest   the outgoing response.
     */
    private void copyHeaders(HttpResponse source, HttpServletResponse dest) {
        for (Header header : source.getAllHeaders()) {
            if (!HEADERS_TO_SKIP.contains(header.getName().toLowerCase())) {
                dest.addHeader(header.getName(), header.getValue());
            }
        }
    }

    /**
     * Copies an incoming response to an outgoing servlet response.
     *
     * @param source the incoming response.
     * @param dest   the outgoing response.
     * @throws IOException if an I/O error occurs.
     */
    private void copyResponse(HttpResponse source,
                              HttpServletResponse dest) throws IOException {
        dest.setStatus(source.getStatusLine().getStatusCode());
        copyHeaders(source, dest);
        IOUtils.copy(source.getEntity().getContent(), dest.getOutputStream());
    }

    /**
     * Builds a string representation of a JSON object indicating that a services call resolution error has occurred.
     *
     * @param msg the error detail message.
     * @return the string representation of the JSON object.
     */
    private String errorJson(String msg) {
        JSONObject json = new JSONObject();
        json.put("status", "failure");
        json.put("action", "PROXY_SERVICE_CALL");
        json.put("error_code", "ERR_BAD_OR_MISSING_FIELD");
        json.put("detail", msg);
        return json.toString(4);
    }

    private String extractServiceName(URL url) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(url.getProtocol()).append("://");
        builder.append(url.getHost());
        if (url.getPort() >= 0) {
            builder.append(":").append(url.getPort());
        }
        return builder.toString();
    }

    /**
     * Sends a response indicating that a services call resolution error has occurred.
     *
     * @param res the outgoing HTTP servlet response.
     * @param msg the error detail message.
     * @throws IOException if an I/O error occurs.
     */
    private void sendErrorResponse(HttpServletResponse res, String msg) throws IOException {
        PrintStream out = new PrintStream(res.getOutputStream());
        try {
            res.setContentType("application/json");
            out.println(errorJson(msg));
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
