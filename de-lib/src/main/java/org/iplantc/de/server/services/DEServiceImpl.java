package org.iplantc.de.server.services;

import static org.iplantc.de.server.AppLoggerConstants.*;
import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.auth.UrlConnector;
import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;
import org.iplantc.de.shared.exceptions.HttpRedirectException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.client.rpc.SerializationException;

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
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * Dispatches HTTP requests to other services.
 *
 * @author jstroot
 */
public class DEServiceImpl implements DEService,
                                      HasHttpServletRequest {
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

    public DEServiceImpl(final ServiceCallResolver serviceResolver,
                         final UrlConnector urlConnector) {
        this.urlConnector = urlConnector;
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
            String address = retrieveServiceAddress(wrapper);
            String endpoint = getEndpointLoggerString(address);
            Logger jsonLogger = LoggerFactory.getLogger(API_JSON_RESPONSE_LOGGER + endpoint);
            Logger apiLogger = LoggerFactory.getLogger(API_REQUEST_LOGGER + endpoint);
            MDC.put(RESPONSE_ENDPOINT_KEY, endpoint.replace(".","/"));

            CloseableHttpClient client = HttpClients.createDefault();
            try {
                json = getResponseBody(getResponse(client, wrapper, address));
                if (jsonLogger.isTraceEnabled()
                        && !Strings.isNullOrEmpty(json)) {
                    MDC.put(RESPONSE_BODY_KEY, json);
                    jsonLogger.trace("{} {}", wrapper.getType(), wrapper.getAddress(), json);
                }
            } catch (AuthenticationException | HttpException ex) {
                apiLogger.error(ex.getMessage(), ex);
                doLogError(ex);
                throw ex;
            } catch (Exception ex) {
                apiLogger.error(ex.getMessage(), ex);
                LOGGER.error("", ex);
                throw new SerializationException(ex);
            } finally {
                IOUtils.closeQuietly(client);
                MDC.remove(RESPONSE_ENDPOINT_KEY);
                MDC.remove(RESPONSE_BODY_KEY);
            }


        }


        return json;
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
    HttpServletRequest getRequest() {
        return request;
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
        if (LOGGER.isErrorEnabled()) {
            Gson prettyGson = new GsonBuilder().create();
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

    private String getEndpointLoggerString(final String address){
        int slashSlash = address.indexOf("//") + 2;
        int singleSlash = address.indexOf("/", slashSlash);
        int questionMark = address.contains("?") ? address.indexOf("?") : address.length();

        return address.substring(singleSlash, questionMark).replace("/", ".");
    }

    /**
     * Gets the response for an HTTP connection.
     *
     * @param client  the HTTP client to use.
     * @param wrapper the services call wrapper.
     * @return the response.
     * @throws IOException if an I/O error occurs.
     */
    private HttpResponse getResponse(final HttpClient client,
                                     final ServiceCallWrapper wrapper,
                                     final String resolvedAddress)
        throws IOException {

        String endpoint = getEndpointLoggerString(resolvedAddress);

        Logger apiLogger = LoggerFactory.getLogger(API_REQUEST_LOGGER + endpoint);
        Logger jsonReqLogger = LoggerFactory.getLogger(AppLoggerConstants.API_JSON_REQUEST_LOGGER + endpoint);

        MDC.put(REQUEST_KEY, resolvedAddress);
        MDC.put(REQUEST_ENDPOINT_KEY, endpoint.replace(".", "/"));
        MDC.put(REQUEST_METHOD_KEY, wrapper.getType());

        String body = updateRequestBody(wrapper.getBody());
        if (jsonReqLogger.isTraceEnabled()
            && !Strings.isNullOrEmpty(body)) {
            MDC.put(REQUEST_BODY_KEY, body);
            jsonReqLogger.trace("{} {}", wrapper.getType(), resolvedAddress, body);
        }
        apiLogger.info("{} {}", wrapper.getType(), resolvedAddress);

        // Clear MDC
        MDC.remove(REQUEST_KEY);
        MDC.remove(RESPONSE_ENDPOINT_KEY);
        MDC.remove(REQUEST_METHOD_KEY);
        MDC.remove(RESPONSE_BODY_KEY);

        BaseServiceCallWrapper.Type type = wrapper.getType();
        switch (type) {
            case GET:
                return get(client, resolvedAddress);

            case PUT:
                return put(client, resolvedAddress, body);

            case POST:
                return post(client, resolvedAddress, body);

            case DELETE:
                return delete(client, resolvedAddress);

            case PATCH:
                return patch(client, resolvedAddress, body);

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
