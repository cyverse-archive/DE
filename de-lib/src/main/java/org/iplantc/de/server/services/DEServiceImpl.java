package org.iplantc.de.server.services;

import org.iplantc.de.server.DEServiceInputStream;
import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.auth.UrlConnector;
import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;
import org.iplantc.de.shared.exceptions.HttpRedirectException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Dispatches HTTP requests to other services.
 *
 * @author jstroot
 */
public class DEServiceImpl extends RemoteServiceServlet implements DEService {
    private static final long serialVersionUID = 1L;
    private final Logger LOGGER = LoggerFactory.getLogger(DEServiceImpl.class);

    /**
     * The current servlet request.
     */
    private HttpServletRequest request = null;

    private ServiceCallResolver serviceResolver;

    /**
     * Used to establish URL connections.
     */
    private UrlConnector urlConnector;

    public DEServiceImpl() {

    }

    public DEServiceImpl(ServiceCallResolver serviceResolver) {
        LOGGER.trace("CONSTRUCTOR CALLED!!");
        this.serviceResolver = serviceResolver;
    }

    /**
     * Implements entry point for services dispatcher.
     *
     * @param wrapper the services call wrapper.
     * @return the response from the services call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws SerializationException  if any other error occurs.
     */
    @Override
    public String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException,
                                                                    HttpException {
        String json = null;

        if (isValidServiceCall(wrapper)) {
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                json = getResponseBody(getResponse(client, wrapper));
            } catch (AuthenticationException | HttpRedirectException ex) {
                doLogError(ex);
                throw ex;
            } catch (HttpException ex) {
                doLogError(ex);
                throw new SerializationException(ex.getResponseBody(), ex);
            } catch (Exception ex) {
                LOGGER.error("", ex);
                throw new SerializationException(ex);
            } finally {
                IOUtils.closeQuietly(client);
            }
        }

        if (LOGGER.isTraceEnabled()
                && json != null) {
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            LOGGER.trace("\nRESPONSE: {} {}\n{}", wrapper.getType(), wrapper.getAddress(), prettyGson.toJson(new JsonParser().parse(json)));
        }

        return json;
    }

    /**
     * Implements entry point for services dispatcher for streaming data back to client.
     *
     * @param wrapper the services call wrapper.
     * @return an input stream that can be used to retrieve the response from the services call.
     * @throws AuthenticationException if the user isn't authenticated.
     * @throws IOException             if an I/O error occurs.
     * @throws SerializationException  if any other error occurs.
     */
    public DEServiceInputStream getServiceStream(ServiceCallWrapper wrapper)
        throws SerializationException, IOException {
        if (isValidServiceCall(wrapper)) {
            CloseableHttpClient client = HttpClients.createDefault();
            try {
                HttpResponse response = getResponse(client, wrapper);
                checkResponse(response);
                return new DEServiceInputStream(client, response);
            } catch (HttpRedirectException | AuthenticationException ex) {
                client.close();
                doLogError(ex);
                throw ex;
            } catch (Exception ex) {
                client.close();
                doLogError(ex);
                throw new SerializationException(ex);
            }
        }

        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    /**
     * Sets the current servlet request.
     *
     * @param request the request to use.
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setServiceResolver(ServiceCallResolver serviceResolver) {
        LOGGER.trace("Set serviceCallResolver = {}", serviceResolver.getClass().getSimpleName());
        this.serviceResolver = serviceResolver;
    }

    @Override
    protected void onAfterResponseSerialized(String serializedResponse) {
        super.onAfterResponseSerialized(serializedResponse);
        MDC.clear();
    }

    @Override
    protected String readContent(HttpServletRequest request) throws ServletException, IOException {
        final Object usernameAttr = request.getSession().getAttribute("username");
        if (usernameAttr != null) {
            MDC.put("username", usernameAttr.toString());
        }
        return super.readContent(request);
    }

    /**
     * Gets the current servlet request.
     *
     * @return the request to use.
     */
    HttpServletRequest getRequest() {
        return request == null ? getThreadLocalRequest() : request;
    }

    /**
     * Sets the URL connector for this services dispatcher. This connector should be set once when the
     * object is created.
     *
     * @param urlConnector the new URL connector.
     */
    @Autowired
    void setUrlConnector(UrlConnector urlConnector) {
        this.urlConnector = urlConnector;
        LOGGER.trace("Set urlConnector = {}", urlConnector.getClass().getSimpleName());
    }

    /**
     * Allows concrete services dispatchers to update the request body.
     *
     * @param body the request body.
     * @return the updated request body.
     */
    String updateRequestBody(String body) {
        return body;
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
     * Creates an HTTP request entity containing a string.
     *
     * @param body the request body.
     * @return the request entity.
     */
    private HttpEntity createEntity(String body) {
        return new StringEntity(body, ContentType.APPLICATION_JSON);
    }

    /**
     * Sends an HTTP DELETE request to another services.
     *
     * @param client  the HTTP client to use.
     * @param address the address to connect to.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse delete(HttpClient client, String address) throws IOException {
        HttpDelete clientRequest = urlConnector.deleteRequest(getRequest(), address);
        return client.execute(clientRequest);
    }

    private void doLogError(Exception ex) {
        if (LOGGER.isDebugEnabled()) {
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String errMsg = ex.getMessage();
            try {
                errMsg = prettyGson.toJson(new JsonParser().parse(ex.getMessage()));
            } catch (JsonSyntaxException ignored) {
            }
            LOGGER.error(errMsg, ex);
        }
    }

    /**
     * Sends an HTTP GET request to another services.
     *
     * @param client  the HTTP client to use.
     * @param address the address to connect to.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse get(HttpClient client, String address) throws IOException {
        final HttpGet getRequest = urlConnector.getRequest(getRequest(), address);
        return client.execute(getRequest);
    }

    /**
     * Gets the response for an HTTP connection.
     *
     * @param client  the HTTP client to use.
     * @param wrapper the services call wrapper.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse getResponse(HttpClient client, ServiceCallWrapper wrapper)
        throws IOException {
        String address = retrieveServiceAddress(wrapper);
        String body = updateRequestBody(wrapper.getBody());
        if (LOGGER.isTraceEnabled()) {
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            LOGGER.trace("\n{} {}\nRequest JSON:\n{}", wrapper.getType(), address, prettyGson.toJson(new JsonParser().parse(body)));
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
     * Verifies that a string is not null or empty.
     *
     * @param in the string to validate.
     * @return true if the string is not null or empty.
     */
    private boolean isValidString(String in) {
        return (in != null && in.length() > 0);
    }

    /**
     * Sends an HTTP PATCH request to another services.
     *
     * @param client  the HTTP client to use.
     * @param address the address to send the request to.
     * @param body    the request body.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse patch(HttpClient client, String address, String body) throws IOException {
        HttpPatch clientRequest = urlConnector.patchRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        return client.execute(clientRequest);
    }

    /**
     * Sends an HTTP POST request to another services.
     *
     * @param client  the HTTP client to use.
     * @param address the address to connect to.
     * @param body    the request body.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse post(HttpClient client, String address, String body) throws IOException {
        HttpPost clientRequest = urlConnector.postRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        return client.execute(clientRequest);
    }

    /**
     * Sends an HTTP PUT request to another services.
     *
     * @param client  the HTTP client to use.
     * @param address the address to connect to.
     * @param body    the request body.
     * @return the response.
     * @throws IOException if an error occurs.
     */
    private HttpResponse put(HttpClient client, String address, String body) throws IOException {
        HttpPut clientRequest = urlConnector.putRequest(getRequest(), address);
        clientRequest.setEntity(createEntity(body));
        return client.execute(clientRequest);
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

}
