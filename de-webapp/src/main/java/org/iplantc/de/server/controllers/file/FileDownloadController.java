package org.iplantc.de.server.controllers.file;

import static org.iplantc.de.server.AppLoggerConstants.REQUEST_KEY;
import static org.iplantc.de.server.AppLoggerConstants.REQUEST_METHOD_KEY;
import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.util.CasUtils;

import com.google.common.collect.Lists;

import org.apache.log4j.MDC;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
    public void doSecureFileDownload(@RequestParam("user") final String user,
                                     @RequestParam("path") final String path,
                                     @RequestParam(value = "attachment",
                                                   required = false,
                                                   defaultValue = "1") final String attachment,
                                     @RequestParam(value = "url",
                                                   required = false,
                                                   defaultValue = "") final String url,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response) throws IOException {

        final RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        // Create and add request factory
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // Set appropriate headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<byte[]> responseEntity = new HttpEntity<>(headers);

        // Create URI template for REST request
        String downloadUrl = url.isEmpty() ? fileIoBaseUrl : dataMgmtServiceBaseUrl + url;

        MDC.put(REQUEST_KEY, downloadUrl + "?path=" + path);
        MDC.put(REQUEST_METHOD_KEY, "GET");
        API_REQUEST_LOG.info("GET {}?path={}&attachment={}", downloadUrl, path, attachment);
        final String uriTemplate = "{downloadUrl}?proxyToken={token}&path={path}&attachment={attachment}";
        AttributePrincipal principal = CasUtils.attributePrincipalFromServletRequest(request);
        String proxyToken = principal.getProxyTicketFor(extractServiceName(new URL(downloadUrl)));

        // Make request
        final ResponseEntity<byte[]> restResponse = restTemplate.exchange(uriTemplate, HttpMethod.GET, responseEntity, byte[].class, downloadUrl, proxyToken, path, attachment);

        if(restResponse.getStatusCode() == HttpStatus.OK){
            if(restResponse.getHeaders().getContentType() != null){
                response.setHeader(HttpHeaders.CONTENT_TYPE, restResponse.getHeaders().getContentType().toString());
            }
            if(restResponse.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION)){
                final List<String> contentDisposition = restResponse.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.get(contentDisposition.size() - 1));
            }
            StreamUtils.copy(restResponse.getBody(), response.getOutputStream());
        }

        MDC.remove(REQUEST_KEY);
        MDC.remove(REQUEST_METHOD_KEY);

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

}
