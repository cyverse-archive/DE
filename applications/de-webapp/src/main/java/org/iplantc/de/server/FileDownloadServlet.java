package org.iplantc.de.server;

import org.iplantc.de.server.services.DEServiceImpl;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for downloading a file.
 */
public class FileDownloadServlet extends HttpServlet {
    private static final String[] HEADER_FIELDS_TO_COPY = {"Content-Disposition"};

    private static final Logger LOG = LoggerFactory.getLogger(FileDownloadServlet.class);

    /**
     * Used to resolve aliased service calls.
     */
    private ServiceCallResolver serviceResolver;

    /**
     * Used to obtain some configuration settings.
     */
    private DiscoveryEnvironmentProperties deProps;

    /**
     * The default constructor.
     */
    public FileDownloadServlet() {}

    /**
     * Initializes the servlet if it hasn't already been initialized.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if any required dependency can't be found.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (serviceResolver == null && deProps == null) {
            serviceResolver = ServiceCallResolver.getServiceCallResolver(getServletContext());
            try {
                deProps = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties();
            } catch (IOException e) {
               throw new ServletException(e);
            }
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        DEServiceInputStream fileContents = null;
        try {
            String address = buildRequestAddress(request);
            ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
            DEServiceImpl dispatcher = createServiceDispatcher(request);

            LOG.debug("doGet - Making service call.");
            fileContents = dispatcher.getServiceStream(wrapper);
            copyHeaderFields(response, fileContents);
            copyFileContents(response, fileContents);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        } finally {
            if (fileContents != null) {
                try {
                    fileContents.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Copies the file contents from the given input stream to the output stream controlled by the given
     * response object.
     *
     * @param response the HTTP servlet response object.
     * @param fileContents the input stream used to retrieve the file contents.
     * @throws IOException if an I/O error occurs.
     */
    private void copyFileContents(HttpServletResponse response, InputStream fileContents)
            throws IOException {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            IOUtils.copyLarge(fileContents, out);
        } finally {
            fileContents.close();
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Copies the content type along with any other HTTP header fields that are supposed to be copied
     * from the original HTTP response to our HTTP servlet response.
     *
     * @param response our HTTP servlet response.
     * @param fileContents the file contents along with the HTTP headers and content type.
     */
    private void copyHeaderFields(HttpServletResponse response, DEServiceInputStream fileContents) {
        String contentType = fileContents.getContentType();
        response.setContentType(contentType == null ? "" : contentType);

        for (String fieldName : HEADER_FIELDS_TO_COPY) {
            response.setHeader(fieldName, fileContents.getHeaderField(fieldName));
        }
    }

    /**
     * Creates the service dispatcher that will be used to fetch the file contents.
     *
     * @param request our HTTP servlet request.
     * @return the service dispatcher.
     */
    private DEServiceImpl createServiceDispatcher(HttpServletRequest request) {
        DEServiceImpl dispatcher = new DEServiceImpl(serviceResolver);
        try {
            dispatcher.init(getServletConfig());
        } catch (ServletException e) {
            LOG.warn("service dispatcher initialization failed", e);
        }
        dispatcher.setContext(getServletContext());
        dispatcher.setRequest(request);
        return dispatcher;
    }

    /**
     * Builds the URL used to fetch the file contents.
     *
     * @param request out HTTP servlet request.
     * @return the URL.
     * @throws UnsupportedEncodingException
     */
    private String buildRequestAddress(HttpServletRequest request) throws UnsupportedEncodingException {
        String path = URLEncoder.encode(request.getParameter("path"), "UTF-8");

        String attachment = request.getParameter("attachment");
        if (attachment == null) {
            attachment = "1";
        }
        attachment = URLEncoder.encode(attachment, "UTF-8");

        String downloadUrl = request.getParameter("url");
        if (downloadUrl == null) {
            downloadUrl = deProps.getFileIoBaseUrl() + "download";
        } else {
            downloadUrl = deProps.getDataMgmtServiceBaseUrl() + downloadUrl;
        }

        String address = String.format("%s?path=%s&attachment=%s", downloadUrl, path, attachment);
        LOG.debug(address);

        return address;
    }
}
