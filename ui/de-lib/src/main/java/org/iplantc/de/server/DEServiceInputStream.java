package org.iplantc.de.server;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A filter input stream that also stores the HTTP headers from the response.
 */
public class DEServiceInputStream extends FilterInputStream {
    /**
     * The HTTP client.
     */
    private final CloseableHttpClient client;

    /**
     * The MIME content type.
     */
    private final String contentType;

    /**
     * The HTTP headers.
     */
    private final Header[] httpHeaders;

    /**
     * Extracts the content type from the response headers.
     *
     * @param response the HTTP response.
     * @return the content type.
     */
    private String extractContentType(HttpResponse response) {
        Header[] matchingHeaders = response.getHeaders("Content-Type");
        return matchingHeaders.length == 0 ? null : matchingHeaders[0].getValue();
    }

    /**
     * Creates a DEServiceInputStream for the given URL connection.
     *
     * @param client the HTTP client.
     * @param response the HTTP response.
     * @throws IOException if an I/O error occurs.
     */
    public DEServiceInputStream(CloseableHttpClient client, HttpResponse response) throws IOException {
        super(response.getEntity().getContent());
        this.client = client;
        contentType = extractContentType(response);
        httpHeaders = response.getAllHeaders();
    }

    /**
     * Gets the MIME content type returned by the server.
     * 
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the value of the HTTP header field with the given name. If the named HTTP header has multiple
     * values then only the last value is returned.
     * 
     * @param fieldName the name of the HTTP header field.
     * @return the value of the HTTP header field or null if the header field doesn't exist.
     */
    public String getHeaderField(String fieldName) {
        List<String> fieldValues = getHeaderValues(fieldName);
        if (fieldValues != null && fieldValues.size() != 0) {
            return fieldValues.get(fieldValues.size() - 1);
        }
        return null;
    }

    /**
     * Gets the list of values for headers matching a given name.
     *
     * @param fieldName the header name.
     * @return the list of header values.
     */
    private List<String> getHeaderValues(String fieldName) {
        List<String> fieldValues = new ArrayList<String>();
        for (int i = 0; i < httpHeaders.length; i++) {
            String headerName = httpHeaders[i].getName();
            if (headerName != null && headerName.equals(fieldName)) {
                fieldValues.add(httpHeaders[i].getValue());
            }
        }
        return fieldValues;
    }

    /**
     * Ensures that the connection manager is shut down when the stream is closed.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        super.close();
        client.close();
    }
}
