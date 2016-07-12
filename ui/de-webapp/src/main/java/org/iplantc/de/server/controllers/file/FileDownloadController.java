package org.iplantc.de.server.controllers.file;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
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
public class FileDownloadController extends DownloadController{

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
        final URI logRequestUri = buildUri(url, path,attachment);

        // Create the request.
        final URI uri = buildUri(url, path, attachment);
        final HttpGet get = new HttpGet(uri);
        processRequest(request, response, logRequestUri, get);

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
    private URI buildUri(final String url, final String path, final String attachment) throws URISyntaxException {

        final String baseUrl = StringUtils.isEmpty(url) ?
                               fileIoBaseUrl :
                               dataMgmtServiceBaseUrl + url + "?attachment=" + attachment;
        final URIBuilder uriBuilder = new URIBuilder(baseUrl).setParameter("path", path);

        return uriBuilder.build();
    }
}
