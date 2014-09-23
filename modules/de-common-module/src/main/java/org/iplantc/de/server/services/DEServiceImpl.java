package org.iplantc.de.server.services;

import org.iplantc.de.server.DEServiceInputStream;
import org.iplantc.de.server.MultipartBodyFactory;
import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.auth.CasUrlConnector;
import org.iplantc.de.server.auth.DESecurityConstants;
import org.iplantc.de.server.auth.UrlConnector;
import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;
import org.iplantc.de.shared.exceptions.HttpRedirectException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.HTTPPart;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dispatches HTTP requests to other services.
 */
public class DEServiceImpl extends RemoteServiceServlet implements DEService {
    private static final long serialVersionUID = 1L;
    private final Logger LOGGER = LoggerFactory.getLogger(DEServiceImpl.class);

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
    public DEServiceImpl() {
        setUrlConnector(new CasUrlConnector());
    }

    /**
     * @param serviceResolver resolves aliased URLs.
     */
    public DEServiceImpl(ServiceCallResolver serviceResolver) {
        this();
        this.serviceResolver = serviceResolver;
    }

    @Override
    protected void onAfterResponseSerialized(String serializedResponse) {
        super.onAfterResponseSerialized(serializedResponse);
        MDC.clear();
    }

    @Override
    protected String readContent(HttpServletRequest request) throws ServletException, IOException {
        MDC.put("username", request.getSession().getAttribute("username").toString());
        return super.readContent(request);
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the services call resolver can't be found.
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
     * Sets the URL connector for this services dispatcher. This connector should be set once when the
     * object is created.
     *
     * @param urlConnector the new URL connector.
     */
    protected void setUrlConnector(UrlConnector urlConnector) {
        this.urlConnector = urlConnector;
    }

    /**
     * Checks the response for an error status.
     *
     * @param response the HTTP response.
     * @throws IOException if an I/O error occurs or the server returns an error status.
     */
    private void checkResponse(HttpResponse response) throws IOException {
        final int status = response.getStatusLine().getStatusCode();
        if (status == 302) {
            final String responseBody = IOUtils.toString(response.getEntity().getContent());
            final String location = response.getFirstHeader(HttpHeaders.LOCATION).getValue();
            throw new HttpRedirectException(status, responseBody, location);
        }
        if (status < 200 || status > 299) {
            throw new HttpException(status, IOUtils.toString(response.getEntity().getContent()));
        }
    }

    /**
     * Reads the response from the server and throws an exception if an error status is returned.
     *
     * @param response the HTTP response.
     * @return the response body.
     * @throws IOException if an I/O error occurs or the server returns an error status.
     */
    private String getResponseBody(HttpResponse response) throws IOException {
        checkResponse(response);
        return IOUtils.toString(response.getEntity().getContent());
    }

    /**
     * Creates an HTTP request entity containing a string.
     *
     * @param body the request body.
     * @return the request entity.
     */
    private HttpEntity createEntity(String body) {
        return new StringEntity(body, ContentType.APPLICATION_JSON);
    }

    /**
     * Sends an HTTP GET request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to connect to.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse get(HttpClient client, String address) throws IOException {
        HttpResponse response = client.execute(urlConnector.getRequest(getRequest(), address));
        return response;
    }

    /**
     * Sends an HTTP POST request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to connect to.
     * @param body the request body.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse post(HttpClient client, String address, String body) throws IOException {
        HttpPost clientRequest = urlConnector.postRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        HttpResponse response = client.execute(clientRequest);
        return response;
    }

    /**
     * Sends an HTTP PUT request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to connect to.
     * @param body the request body.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse put(HttpClient client, String address, String body) throws IOException {
        HttpPut clientRequest = urlConnector.putRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        HttpResponse response = client.execute(clientRequest);
        return response;
    }

    /**
     * Sends an HTTP PATCH request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to send the request to.
     * @param body the request body.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse patch(HttpClient client, String address, String body) throws IOException {
        HttpPatch clientRequest = urlConnector.patchRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        HttpResponse response = client.execute(clientRequest);
        return response;
    }

    /**
     * Sends an HTTP DELETE request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to connect to.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse delete(HttpClient client, String address) throws IOException {
        HttpDelete clientRequest = urlConnector.deleteRequest(getRequest(), address);
        HttpResponse response = client.execute(clientRequest);
        return response;
    }

    /**
     * Sends a multipart HTTP PUT request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to send the request to.
     * @param parts the components of the multipart request.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse putMultipart(HttpClient client, String address, List<HTTPPart> parts)
            throws IOException {
        HttpPut clientRequest = urlConnector.putRequest(getRequest(), address);
        buildMultipartRequest(clientRequest, parts);
        HttpResponse response = client.execute(clientRequest);
        return response;
    }

    /**
     * Sends a multipart HTTP POST request to another services.
     *
     * @param client the HTTP client to use.
     * @param address the address to send the request to.
     * @param parts the components of the multipart request.
     * @return the response body.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse postMultipart(HttpClient client, String address, List<HTTPPart> parts)
            throws IOException {
        HttpPost clientRequest = urlConnector.postRequest(getRequest(), address);
        buildMultipartRequest(clientRequest, parts);
        HttpResponse response = client.execute(clientRequest);
        return response;
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
     * Verifies that a string is not null or empty.
     *
     * @param in the string to validate.
     * @return true if the string is not null or empty.
     */
    private boolean isValidString(String in) {
        return (in != null && in.length() > 0);
    }

    /**
     * Validates a services call wrapper. The address must be a non-empty string for all HTTP requests.
     * The message body must be a non-empty string for PUT and POST requests.
     *
     * @param wrapper the services call wrapper being validated.
     * @return true if the services call wrapper is valid.
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
                    case PATCH:
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
     * Validates a multi-part services call wrapper. The address must not be null or empty and the message
     * body must have at least one part.
     *
     * @param wrapper the wrapper to validate.
     * @return true if the services call wrapper is valid.
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
     * Retrieve the services address for the wrapper.
     *
     * @param wrapper services call wrapper containing metadata for a call.
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
     * Allows concrete services dispatchers to update the request body.
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
     * Gets the response for an HTTP connection.
     *
     * @param client the HTTP client to use.
     * @param wrapper the services call wrapper.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse getResponse(HttpClient client, ServiceCallWrapper wrapper)
            throws IOException {
        String address = retrieveServiceAddress(wrapper);
        String body = updateRequestBody(wrapper.getBody());
        if(LOGGER.isTraceEnabled()){
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            LOGGER.trace("{} {}\nRequest JSON:\n{}", wrapper.getType(), wrapper.getAddress(),prettyGson.toJson(new JsonParser().parse(body)));
        }

        BaseServiceCallWrapper.Type type = wrapper.getType();
        switch (type) {
            case GET:
                return get(client, address);

            case PUT:
                return put(client, address, body);

            case POST:
                return post(client, address, body);

            case DELETE:
                return delete(client, address);

            case PATCH:
                return patch(client, address, body);

            default:
                throw new UnsupportedOperationException("HTTP method " + type + " not supported");
        }
    }

    /**
     * Implements entry point for services dispatcher.
     *
     * @param wrapper the services call wrapper.
     * @return the response from the services call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws SerializationException if any other error occurs.
     */
    @Override
    public String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException,
            HttpException {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            HttpClient client = new DefaultHttpClient();
            try {
                json = getResponseBody(getResponse(client, wrapper));
            } catch (AuthenticationException | HttpRedirectException ex) {
                throw ex;
            } catch (HttpException ex) {
                throw new SerializationException(ex.getResponseBody(), ex);
            } catch (Exception ex) {
                throw new SerializationException(ex);
            } finally {
                client.getConnectionManager().shutdown();
            }
        }

        if(LOGGER.isTraceEnabled()){
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            LOGGER.trace("RESPONSE: {} {}\n{}", wrapper.getType(), wrapper.getAddress(), prettyGson.toJson(new JsonParser().parse(json)));
        }

        return json;
    }

    /**
     * Implements entry point for services dispatcher for streaming data back to client.
     *
     * @param wrapper the services call wrapper.
     * @return an input stream that can be used to retrieve the response from the services call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws IOException if an I/O error occurs.
     * @throws SerializationException if any other error occurs.
     */
    public DEServiceInputStream getServiceStream(ServiceCallWrapper wrapper)
            throws SerializationException, IOException {
        if (isValidServiceCall(wrapper)) {
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = getResponse(client, wrapper);
                checkResponse(response);
                return new DEServiceInputStream(client, response);
            } catch (HttpRedirectException | AuthenticationException ex) {
                client.getConnectionManager().shutdown();
                throw ex;
            } catch (Exception ex) {
                client.getConnectionManager().shutdown();
                throw new SerializationException(ex);
            }
        }

        return null;
    }

    public HttpResponse getResponse(HttpClient client, MultiPartServiceWrapper wrapper)
            throws IOException {
        String address = retrieveServiceAddress(wrapper);
        List<HTTPPart> parts = wrapper.getParts();
        BaseServiceCallWrapper.Type type = wrapper.getType();
        switch (type) {
            case PUT:
                return putMultipart(client, address, parts);

            case POST:
                return postMultipart(client, address, parts);

            default:
                throw new UnsupportedOperationException("HTTP method " + type + " not supported");
        }
    }
    /**
     * Sends a multi-part HTTP PUT or POST request to another services and returns the response.
     *
     * @param wrapper the services call wrapper.
     * @return the response to the HTTP request.
     * @throws SerializationException if an error occurs.
     */
    @Override
    public String getServiceData(MultiPartServiceWrapper wrapper)
            throws SerializationException, AuthenticationException, HttpException {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            HttpClient client = new DefaultHttpClient();
            try {
                json = getResponseBody(getResponse(client, wrapper));
            } catch (AuthenticationException | HttpRedirectException ex) {
                throw ex;
            } catch (HttpException ex) {
                throw new SerializationException(ex.getResponseBody(), ex);
            } catch (Exception ex) {
                throw new SerializationException(ex);
            } finally {
                client.getConnectionManager().shutdown();
            }
        }

        if(LOGGER.isTraceEnabled()){
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            LOGGER.trace("RESPONSE: {} {}\n{}",wrapper.getType(), wrapper.getAddress(), prettyGson.toJson(new JsonParser().parse(json)));
        }
        return json;
    }
}
