package org.iplantc.de.server;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.Arrays;
import java.util.List;

/**
 * Used to build Apache HTTP Client request objects.
 * 
 * @author Dennis Roberts
 */
public class RequestFactory {
    /**
     * The types of methods for which requests can be returned.
     */
    private static final List<String> validMethods = Arrays.asList("POST", "PUT");

    /**
     * Builds the request object.
     * 
     * @param method the HTTP method.
     * @param address the address to connect to.
     * @return the request object.
     */
    public static HttpEntityEnclosingRequestBase buildRequest(String method, String address) {
        if (!validMethods.contains(method)) {
            throw new IllegalArgumentException("supported method types: " + validMethods);
        }
        return method == "POST" ? new HttpPost(address) : new HttpPut(address);
    }
}
