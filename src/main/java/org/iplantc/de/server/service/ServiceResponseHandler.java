package org.iplantc.de.server.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * A response handler that reads the response body from failed service calls.
 *
 * @author Dennis Roberts
 */
public class ServiceResponseHandler implements ResponseHandler<String> {

    /**
     * Handles a response from a service.  If the service call fails then the response body is captured and stored in
     * the resulting exception.
     *
     * @param response the response.
     * @return the response body.
     * @throws ClientProtocolException
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        String responseBody = getResponseBody(response);
        if (status < 200 || status > 299) {
            throw new ServiceCallFailedException("response body: " + responseBody);
        }
        return responseBody;
    }

    /**
     * @param response the HTTP response.
     * @return the response body or the empty string if there is no response body.
     * @throws IOException if an I/O error occurs.
     */
    private String getResponseBody(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return entity == null ? "" : EntityUtils.toString(entity);
    }
}
