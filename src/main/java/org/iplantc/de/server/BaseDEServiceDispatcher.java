package org.iplantc.de.server;

import org.iplantc.de.shared.AuthenticationException;
import org.iplantc.de.shared.DEService;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.HTTPPart;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Dispatches HTTP requests to other services.
 */
public abstract class BaseDEServiceDispatcher extends RemoteServiceServlet implements DEService {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(BaseDEServiceDispatcher.class);

    private ServiceCallResolver serviceResolver;

    /**
     * The servlet context to use when looking up the keystore path.
     */
    private ServletContext context = null;

    /**
     * The current servlet request.
     */
    private HttpServletRequest request = null;

    /**
     * Used to establish URL connections.
     */
    private UrlConnector urlConnector;

    /**
     * The default constructor.
     */
    public BaseDEServiceDispatcher() {}

    /**
     * @param serviceResolver resolves aliased URLs.
     */
    public BaseDEServiceDispatcher(ServiceCallResolver serviceResolver) {
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
     * Sets the servlet context to use when looking up the keystore path.
     *
     * @param context the context.
     */
    public void setContext(ServletContext context) {
        this.context = context;
    }

    /**
     * Gets the servlet context to use when looking up the keystore path.
     *
     * @return an object representing a context for a servlet.
     */
    public ServletContext getContext() {
        return context == null ? getServletContext() : context;
    }

    /**
     * Sets the current servlet request.
     *
     * @param request the request to use.
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Gets the current servlet request.
     *
     * @return the request to use.
     */
    public HttpServletRequest getRequest() {
        return request == null ? getThreadLocalRequest() : request;
    }

    /**
     * Sets the URL connector for this service dispatcher. This connector should be set once when the
     * object is created.
     *
     * @param urlConnector the new URL connector.
     */
    protected void setUrlConnector(UrlConnector urlConnector) {
        this.urlConnector = urlConnector;
    }

    /**
     * Retrieves the result from a URL connection.
     *
     * @param urlc the URL connection.
     * @return the URL result as a string.
     * @throws IOException if an I/O error occurs.
     */
    private String retrieveResult(URLConnection urlc) throws IOException {
        return IOUtils.toString(urlc.getInputStream());
    }

    /**
     * Obtains a URL connection.
     *
     * @param address the address to connect to.
     * @return the URL connection.
     * @throws IOException if the connection can't be established.
     */
    protected HttpURLConnection getUrlConnection(String address) throws IOException {
        if (urlConnector == null) {
            throw new IOException("No URL connector available.");
        }
        return urlConnector.getUrlConnection(getRequest(), address);
    }

    /**
     * Sends an HTTP GET request to another service.
     *
     * @param address the address to connect to.
     * @return the URL connection used to send the request.
     * @throws IOException if an error occurs.
     */
    private URLConnection get(String address) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sending a GET request to " + address);
        }

        // make post mode connection
        URLConnection urlc = getUrlConnection(address);
        urlc.setDoOutput(true);

        LOGGER.debug("GET request sent to " + address);

        return urlc;
    }

    /**
     * Sends an HTTP UPDATE request to another service.
     *
     * @param address the address to connect to.
     * @param body the request body.
     * @param requestMethod the request method.
     * @return the URL connection used to send the request.
     * @throws IOException if an I/O error occurs.
     */
    private URLConnection update(String address, String body, String requestMethod) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sending an UPDATE request to " + address);
        }

        // make post mode connection
        HttpURLConnection urlc = getUrlConnection(address);
        urlc.setRequestMethod(requestMethod);
        urlc.setDoOutput(true);

        // send post
        OutputStreamWriter outRemote = null;
        try {
            outRemote = new OutputStreamWriter(urlc.getOutputStream());
            outRemote.write(body);
            outRemote.flush();
        } finally {
            if (outRemote != null) {
                outRemote.close();
            }
        }

        LOGGER.debug("UPDATE request sent");

        return urlc;
    }

    /**
     * Sends a multipart HTTP update request to another service.
     *
     * @param address the address to send the request to.
     * @param parts the components of the multipart request.
     * @param requestMethod the request method.
     * @return the URL connection used to send the request.
     * @throws IOException if an I/O error occurs.
     */
    private String updateMultipart(String address, List<HTTPPart> parts, String requestMethod)
            throws IOException {
        String result;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sending a multipart UPDATE request to " + address);
        }

        // Build the multipart request.
        HttpEntityEnclosingRequestBase clientRequest = urlConnector.getRequest(getRequest(), address,
                requestMethod);
        buildMultipartRequest(clientRequest, parts);

        // Execute the request.
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(clientRequest);
            result = IOUtils.toString(response.getEntity().getContent());
        } finally {
            client.getConnectionManager().shutdown();
        }

        LOGGER.debug("multipart UPDATE request sent");

        return result;
    }

    private void buildMultipartRequest(HttpEntityEnclosingRequestBase clientRequest, List<HTTPPart> parts)
            throws IOException {
        MultipartEntity entity = new MultipartEntity();
        for (HTTPPart part : parts) {
            entity.addPart(part.getName(), MultipartBodyFactory.createBody(part));
        }
        addAdditionalParts(entity);
        clientRequest.setEntity(entity);
    }

    /**
     * This method allows concrete subclasses to add additional parts to multipart form requests if
     * necessary. By default, no additional parts are added.
     *
     * @param entity the entity to add the part to.
     * @throws IOException if an I/O error occurs.
     */
    protected void addAdditionalParts(MultipartEntity entity) throws IOException {
        // The base implementation of this method does nothing.
    }

    /**
     * Sends an HTTP DELETE request to another service.
     *
     * @param address the address to send the request to.
     * @return the URL connection used to send the request.
     * @throws IOException if an I/O error occurs.
     */
    private URLConnection delete(String address) throws IOException {
        // make post mode connection
        HttpURLConnection urlc = getUrlConnection(address);

        urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlc.setRequestMethod("DELETE");
        urlc.setDoOutput(true);
        urlc.connect();

        return urlc;
    }

    /**
     * Verifies that a string is not null or empty.
     *
     * @param in the string to validate.
     * @return true if the string is not null or empty.
     */
    private boolean isValidString(String in) {
        return (in != null && in.length() > 0);
    }

    /**
     * Validates a service call wrapper. The address must be a non-empty string for all HTTP requests.
     * The message body must be a non-empty string for PUT and POST requests.
     *
     * @param wrapper the service call wrapper being validated.
     * @return true if the service call wrapper is valid.
     */
    private boolean isValidServiceCall(ServiceCallWrapper wrapper) {
        boolean ret = false; // assume failure

        if (wrapper != null) {
            if (isValidString(wrapper.getAddress())) {
                switch (wrapper.getType()) {
                    case GET:
                    case DELETE:
                        ret = true;
                        break;

                    case PUT:
                    case POST:
                        if (isValidString(wrapper.getBody())) {
                            ret = true;
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        return ret;
    }

    /**
     * Validates a multi-part service call wrapper. The address must not be null or empty and the message
     * body must have at least one part.
     *
     * @param wrapper the wrapper to validate.
     * @return true if the service call wrapper is valid.
     */
    private boolean isValidServiceCall(MultiPartServiceWrapper wrapper) {
        boolean ret = false; // assume failure

        if (wrapper != null) {
            if (isValidString(wrapper.getAddress())) {
                switch (wrapper.getType()) {
                    case PUT:
                    case POST:
                        if (wrapper.getNumParts() > 0) {
                            ret = true;
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        return ret;
    }

    /**
     * Retrieve the service address for the wrapper.
     *
     * @param service call wrapper containing metadata for a call.
     * @return a string representing a valid URL.
     */
    private String retrieveServiceAddress(BaseServiceCallWrapper wrapper) {
        String address = serviceResolver.resolveAddress(wrapper);
        if (wrapper.hasArguments()) {
            String args = wrapper.getArguments();
            address += (args.startsWith("?")) ? args : "?" + args;
        }
        return address;
    }

    /**
     * Allows concrete service dispatchers to update the request body.
     *
     * @param body the request body.
     * @return the updated request body.
     */
    protected String updateRequestBody(String body) {
        return body;
    }

    /**
     * Gets the name of the authenticated user.
     *
     * @return the username as a string.
     * @throws IOException if the username can't be obtained.
     */
    protected String getUsername() throws IOException {
        Object username = getRequest().getSession().getAttribute(DESecurityConstants.LOCAL_SHIB_UID);
        if (username == null) {
            throw new IOException("user is not authenticated");
        }
        return username.toString();
    }

    /**
     * Implements entry point for service dispatcher.
     *
     * @param wrapper the service call wrapper.
     * @return the response from the service call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws SerializationException if any other error occurs.
     */
    @Override
    public String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException {
        String json = null;
        URLConnection urlc = null;

        if (isValidServiceCall(wrapper)) {
            String address = retrieveServiceAddress(wrapper);
            String body = updateRequestBody(wrapper.getBody());
            LOGGER.debug("request json==>" + body);
            try {
                switch (wrapper.getType()) {
                    case GET:
                        urlc = get(address);
                        break;

                    case PUT:
                        urlc = update(address, body, "PUT");
                        break;

                    case POST:
                        urlc = update(address, body, "POST");
                        break;

                    case DELETE:
                        urlc = delete(address);
                        break;

                    default:
                        break;
                }

                json = retrieveResult(urlc);
            } catch (AuthenticationException ex) {
                throw ex;
            } catch (Exception ex) {
                LOGGER.error(ex.toString(), ex);
                // because the GWT compiler will issue a warning if we simply
                // throw exception, we'll
                // use SerializationException()
                SerializationException exception;

                try {
                    HttpURLConnection httpConn = (HttpURLConnection)urlc;

                    String errMsg = IOUtils.toString(httpConn.getErrorStream());

                    LOGGER.error(errMsg);

                    exception = new SerializationException(errMsg, ex);
                } catch (Exception ignore) {
                    exception = new SerializationException(ex);
                }

                throw exception;
            }
        }

        LOGGER.debug("json==>" + json);
        return json;
    }

    /**
     * Implements entry point for service dispatcher for streaming data back to client.
     *
     * @param wrapper the service call wrapper.
     * @return an input stream that can be used to retrieve the response from the service call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws IOException if an I/O error occurs.
     * @throws SerializationException if any other error occurs.
     */
    public DEServiceInputStream getServiceStream(ServiceCallWrapper wrapper)
            throws SerializationException, IOException {
        String json = null;
        URLConnection urlc = null;

        if (isValidServiceCall(wrapper)) {
            String address = retrieveServiceAddress(wrapper);
            String body = updateRequestBody(wrapper.getBody());

            try {
                switch (wrapper.getType()) {
                    case GET:
                        urlc = get(address);
                        break;

                    case PUT:
                        urlc = update(address, body, "PUT");
                        break;

                    case POST:
                        urlc = update(address, body, "POST");
                        break;

                    case DELETE:
                        urlc = delete(address);
                        break;

                    default:
                        break;
                }
            } catch (AuthenticationException ex) {
                throw ex;
            } catch (Exception ex) {
                // because the GWT compiler will issue a warning if we simply
                // throw exception, we'll
                // use SerializationException()
                throw new SerializationException(ex);
            }
        }

        LOGGER.debug("json==>" + json);
        System.out.println("json==>" + json);
        return new DEServiceInputStream(urlc);
    }

    /**
     * Sends a multi-part HTTP PUT or POST request to another service and returns the response.
     *
     * @param wrapper the service call wrapper.
     * @return the response to the HTTP request.
     * @throws SerializationException if an error occurs.
     */
    @Override
    public String getServiceData(MultiPartServiceWrapper wrapper)
            throws SerializationException, AuthenticationException {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            String address = retrieveServiceAddress(wrapper);
            List<HTTPPart> parts = wrapper.getParts();

            try {
                switch (wrapper.getType()) {
                    case PUT:
                        json = updateMultipart(address, parts, "PUT");
                        break;

                    case POST:
                        json = updateMultipart(address, parts, "POST");
                        break;

                    default:
                        break;
                }
            } catch (AuthenticationException ex) {
                throw ex;
            } catch (Exception ex) {
                // because the GWT compiler will issue a warning if we simply
                // throw exception, we'll
                // use SerializationException()
                throw new SerializationException(ex);
            }
        }

        System.out.println("json==>" + json);
        return json;
    }
}
