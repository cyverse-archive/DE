package org.iplantc.de.server.controllers.file;

import static org.iplantc.de.server.AppLoggerConstants.RESPONSE_KEY;

import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.AppLoggerUtil;
import org.iplantc.de.server.auth.DESecurityConstants;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
 * Created by sriram on 6/28/16.
 */

@Controller
public class MetadataTemplateDownloadController extends DownloadController{

    private final String METADATA_TEMPLATE = "metadata/template/";

    private final String CSV ="/blank-csv";

    private final Logger logger = LoggerFactory.getLogger(MetadataTemplateDownloadController.class);

    @RequestMapping(value = "/de/secured/mdTemplateDownload", method = RequestMethod.GET)
    public void doSecureFileDownload(@RequestParam("template_id") final String template_id,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response)
            throws IOException, URISyntaxException {
        // Prepare to process the request.
        final URI logRequestUri = buildUri(template_id);

        // Create the request.
        final URI uri = buildUri(template_id);
        final HttpGet get = new HttpGet(uri);
        processRequest(request, response, logRequestUri, get);
    }

    private URI buildUri(String template_id) throws URISyntaxException {
        String baseUrl = dataMgmtServiceBaseUrl + METADATA_TEMPLATE + template_id + CSV;
        URIBuilder ub = new URIBuilder(baseUrl);
        return ub.build();
    }


}
