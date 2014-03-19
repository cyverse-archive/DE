package org.iplantc.de.server;

import java.io.FilterInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * A filter input stream that also stores the HTTP headers from the response.
 */
public class DEServiceInputStream extends FilterInputStream {
    /**
     * The MIME content type.
     */
    private String contentType;

    /**
     * The HTTP headers.
     */
    private Map<String, List<String>> httpHeaders;

    /**
     * Creates a DEServiceInputStream for the given URL connection.
     * 
     * @param urlConnection the URL connection.
     * @throws IOException if an I/O error occurs.
     */
    public DEServiceInputStream(URLConnection urlConnection) throws IOException {
        super(urlConnection.getInputStream());
        contentType = urlConnection.getContentType();
        httpHeaders = urlConnection.getHeaderFields();
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
        List<String> fieldValues = httpHeaders.get(fieldName);
        if (fieldValues != null && fieldValues.size() != 0) {
            return fieldValues.get(fieldValues.size() - 1);
        }
        return null;
    }
}
