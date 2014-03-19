package org.iplantc.de.server.service;

import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.UnresolvableServiceNameException;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A secured servlet that forwards requests directly to other services.
 *
 * @author Dennis Roberts
 */
public class ProxyServlet extends HttpServlet {
    private static final long serialVersionUID = -8343592394048255113L;

    /**
     * The set of headers that should be skipped when copying headers.
     */
    private static final Set<String> HEADERS_TO_SKIP = new HashSet<String>(Arrays.asList("content-length"));

    /**
     * Used to resolve aliased service calls.
     */
    private ServiceCallResolver serviceResolver;

    /**
     * The default constructor.
     */
    public ProxyServlet() {
    }

    /**
     * @param serviceResolver used to resolve aliased service calls.
     */
    public ProxyServlet(ServiceCallResolver serviceResolver) {
        this.serviceResolver = serviceResolver;
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the service call resolver can't be found.
     */
    @Override
    public void init() throws ServletException {
        if (serviceResolver == null) {
            serviceResolver = ServiceCallResolver.getServiceCallResolver(getServletContext());
        }
    }

    /**
     * Forwards an HTTP DELETE request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpDelete(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP GET request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpGet(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP HEAD request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpHead(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP OPTIONS request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doOptions(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpOptions(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP POST request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpPost(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP PUT request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpPut(uri));
            }
        }.call();
    }

    /**
     * Forwards an HTTP TRACE request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper(req, res) {
            @Override
            protected void forwardRequest(String uri) throws IOException {
                forwardRequest(new HttpTrace(uri));
            }
        }.call();
    }

    /**
     * Resolves an aliased service call.
     *
     * @param req the original HTTP servlet request.
     * @return the string representation of the URI to forward the request to.
     */
    private String resolveServiceCall(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            throw new NoServiceNameProvidedException();
        }
        String serviceName = pathInfo.replaceAll("^/", "");
        return serviceResolver.resolveAddress(serviceName);
    }

    /**
     * Wraps a service call resolution so that the servlet returns an appropriate response whenever a servlet can't be
     * resolved.
     */
    private abstract class ServiceCallResolutionWrapper {

        /**
         * The incoming HTTP servlet request.
         */
        private final HttpServletRequest req;

        /**
         * The outgoing HTTP servlet response.
         */
        private final HttpServletResponse res;

        /**
         * @param req the incoming HTTP servlet request.
         * @param res the outgoing HTTP servlet response.
         */
        public ServiceCallResolutionWrapper(HttpServletRequest req, HttpServletResponse res) {
            this.req = req;
            this.res = res;
        }

        /**
         * Calls the service call resolution wrapper.
         *
         * @throws IOException if an I/O error occurs.
         */
        public void call() throws IOException {
            String uri;
            try {
                uri = resolveServiceCall(req);
                String queryString = req.getQueryString();
                if (queryString != null) {
                    uri += "?" + queryString;
                }
            }
            catch (NoServiceNameProvidedException e) {
                sendErrorResponse(res, e.getMessage());
                return;
            }
            catch (UnresolvableServiceNameException e) {
                sendErrorResponse(res, e.getMessage());
                return;
            }
            forwardRequest(uri);
        }

        /**
         * Forwards the request to the named service.
         *
         * @param uri the URI to use to connect to the named service.
         * @throws IOException if an I/O error occurs.
         */
        protected abstract void forwardRequest(String uri) throws IOException;

        /**
         * Forwards a request that cannot contain a request body (for example, a GET or DELETE request).
         *
         * @param out the outgoing request.
         * @throws IOException if an I/O error occurs.
         */
        protected void forwardRequest(HttpRequestBase out) throws IOException {
            HttpClient client = new DefaultHttpClient();
            try {
                copyHeaders(req, out);
                copyResponse(client.execute(out), res);
            }
            finally {
                out.releaseConnection();
            }
        }

        /**
         * Forwards a request that can contain a request bod (for example, a POST or PUT request).
         *
         * @param out the outgoing request.
         * @throws IOException if an I/O exception occurs.
         */
        protected void forwardRequest(HttpEntityEnclosingRequestBase out) throws IOException {
            HttpClient client = new DefaultHttpClient();
            try {
                copyHeaders(req, out);
                out.setEntity(new InputStreamEntity(req.getInputStream(), req.getContentLength()));
                copyResponse(client.execute(out), res);
            }
            finally {
                out.releaseConnection();
            }
        }

        /**
         * Copies an incoming response to an outgoing servlet response.
         *
         * @param source the incoming response.
         * @param dest the outgoing response.
         * @throws IOException if an I/O error occurs.
         */
        private void copyResponse(HttpResponse source, HttpServletResponse dest) throws IOException {
            dest.setStatus(source.getStatusLine().getStatusCode());
            copyHeaders(source, dest);
            IOUtils.copy(source.getEntity().getContent(), dest.getOutputStream());
        }

        /**
         * Copies all response headers from an incoming response to an outgoing servlet response.
         *
         * @param source the incoming response.
         * @param dest the outgoing response.
         */
        private void copyHeaders(HttpResponse source, HttpServletResponse dest) {
            for (Header header : source.getAllHeaders()) {
                if (!HEADERS_TO_SKIP.contains(header.getName().toLowerCase())) {
                    dest.addHeader(header.getName(), header.getValue());
                }
            }
        }

        /**
         * Copies all request headers from an incoming servlet request to an outgoing request.
         *
         * @param source the incoming request.
         * @param dest the outgoing request.
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
         * Sends a response indicating that a service call resolution error has occurred.
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
            }
            finally {
                if (out != null) {
                    IOUtils.closeQuietly(out);
                }
            }
        }

        /**
         * Builds a string representation of a JSON object indicating that a service call resolution error has occurred.
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
    }
}
