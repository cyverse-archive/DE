package org.iplantc.de.server.services;

import static org.iplantc.de.server.AppLoggerConstants.RESPONSE_KEY;
import org.iplantc.de.server.AppLoggerConstants;
import org.iplantc.de.server.AppLoggerUtil;
import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.auth.UrlConnector;
import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;
import org.iplantc.de.shared.exceptions.HttpRedirectException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.SerializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * Dispatches HTTP requests to other services.
 *
 * @author jstroot
 */
public class DEServiceImpl implements DEService,
                                      HasHttpServletRequest {
    private final Logger API_METRICS_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);
    private final AppLoggerUtil loggerUtil = AppLoggerUtil.getInstance();

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
            String endpoint = getEndpointFromRequestAddress(address);

            CloseableHttpClient client = HttpClients.createDefault();
            try {
                final long requestStartTime = System.currentTimeMillis();
                final HttpResponse response = getResponse(client, wrapper, address);
                final long requestEndTime = System.currentTimeMillis();
                json = getResponseBody(response);
                final Map<String, Object> responseMap = loggerUtil.updateMdcResponseMap(response,
                                                                                        wrapper.getType(),
                                                                                        endpoint,
                                                                                        json,
                                                                                        requestEndTime - requestStartTime);
                ObjectMapper mapper = new ObjectMapper();
                String responseAsString = mapper.writeValueAsString(responseMap);
                MDC.put(RESPONSE_KEY, responseAsString);
                API_METRICS_LOG.info("{} {}", wrapper.getType(), endpoint);

            } catch (AuthenticationException | HttpException ex) {
                API_METRICS_LOG.error(wrapper.getType() + " " + endpoint, ex);
                throw ex;
            } catch (Exception ex) {
                API_METRICS_LOG.error(wrapper.getType() + " " + endpoint, ex);
                throw new SerializationException(ex);
            } finally {
                IOUtils.closeQuietly(client);
                MDC.remove(RESPONSE_KEY);
            }
        }
        return json;
    }

    @Override
    public String getServiceData(ServiceCallWrapper wrapper,
                                 HashMap<String, String> extraLoggerMdcItems) throws SerializationException, AuthenticationException, HttpException {
        final Set<Map.Entry<String, String>> entries = extraLoggerMdcItems.entrySet();
        for(Map.Entry<String, String> entry : entries){
            MDC.put(entry.getKey(), entry.getValue());
        }
        String ret = null;
        try {
            ret = getServiceData(wrapper);
        } catch (SerializationException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw e;
        } catch (HttpException e) {
            throw e;
        } finally {
            for(Map.Entry<String, String> entry : entries){
                MDC.remove(entry.getKey());
            }
        }
        return ret;
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

    private String getEndpointFromRequestAddress(final String address){
        int slashSlash = address.indexOf("//") + 2;
        int singleSlash = address.indexOf("/", slashSlash);
        int questionMark = address.contains("?") ? address.indexOf("?") : address.length();

        return address.substring(singleSlash, questionMark);
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

        String body = updateRequestBody(wrapper.getBody());
        // Add request body to MDC
        loggerUtil.addBodyToMdcRequestMap(wrapper.getBody());

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
