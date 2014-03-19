package org.iplantc.de.server.util;

import org.iplantc.de.server.DESecurityConstants;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * A simple utility class for dealing with URLs.
 *
 * @author Dennis Roberts
 */
public class UrlUtils {

	/**
	 * Prevent instantiation.
	 */
	private UrlUtils() {
	}

    /**
     * We assume that URLs are absolute URLs if they contain "://".
     */
    private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("://");

    /**
     * Adds query string parameters to a URL.
     *
     * @param url the URL.
     * @param parms the query string parameters to add.
     * @return the updated URL.
     * @throws IOException if the resulting URL is invalid.
     */
	public static URL addQueryParameters(URL url, String... parms) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(extractProtocol(url));
		builder.append(url.getHost());
		builder.append(extractPort(url));
		builder.append(extractPath(url));
		builder.append(addParameters(extractQuery(url), parms));
		return new URL(builder.toString());
	}

    /**
     * Adds parameters to a query string.
     *
     * @param queryString the original query string.
     * @param parms the parameters to add to the query string.
     * @return the updated query string.
     */
	private static String addParameters(String queryString, String... parms) {
		StringBuilder builder = new StringBuilder(queryString);
        for (String parm : parms) {
            builder.append(builder.length() == 0 ? "?" : "&");
            builder.append(parm);
        }
        return builder.toString();
	}

	/**
     * Builds a new URL based on the given URL, but with a different host.
     *
     * @param url the URL to modify.
     * @param newHost the new host name or IP address to put in the URL.
     * @return the URL with the new host name.
     * @throws IOException if the original URL or the newly formed URL is invalid.
     */
    public static URL replaceHost(URL url, String newHost) throws IOException {
        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append(extractProtocol(url));
        urlBuffer.append(DESecurityConstants.ASSERTION_QUERY_HOST);
        urlBuffer.append(extractPort(url));
        urlBuffer.append(extractPath(url));
        urlBuffer.append(extractQuery(url));
        return new URL(urlBuffer.toString());
    }

    /**
     * Extracts the query from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the query or the empty string if there was no query in the original URL.
     */
    private static String extractQuery(URL originalUrl) {
        String query = originalUrl.getQuery();
        return query == null ? "" : "?" + query;
    }

    /**
     * Extracts the path from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the path or the empty string if there was no path in the original URL.
     */
    private static Object extractPath(URL originalUrl) {
        String path = originalUrl.getPath();
        return path == null ? "" : path;
    }

    /**
     * Extracts the port from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the port or the empty string if there was no port in the original URL.
     */
    private static Object extractPort(URL originalUrl) {
        int port = originalUrl.getPort();
        return port < 0 ? "" : ":" + port;
    }

    /**
     * Extracts the protocol from the given URL.
     *
     * @param originalUrl the URL that was provided to us by Shibboleth.
     * @return the protocol string, including the colon and slashes.
     */
    private static Object extractProtocol(URL originalUrl) {
        return originalUrl.getProtocol() + "://";
    }

    /**
     * Converts a relative URL to an absolute URL. If the URL is already an absolute URL then this method does nothing.
     *
     * @param contextPath the path to the current location.
     * @param originalUrl the original URL.
     * @return
     */
    public static String convertRelativeUrl(String contextPath, String originalUrl) {
        if (ABSOLUTE_URL_PATTERN.matcher(originalUrl).find()) {
            return originalUrl;
        }
        else {
            return contextPath.replaceAll("/$", "") + "/" + originalUrl.replaceAll("^/", "");
        }
    }
}
