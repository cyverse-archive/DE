package org.iplantc.de.server;

/**
 * This interface holds constants for various loggers used throughout the application.
 *
 *
 * Each API endpoint has it's own logger which starts with {@code de.ui.api}.
 * For instance, turning on logging for {@code de.ui.api.json.request.apps} will only reflect
 * JSON payload activity on the 'apps' endpoint.
 *
 *
 * Created by jstroot on 4/30/15.
 * @author jstroot
 */
public interface AppLoggerConstants {
    /**
     * Tracks the applications DE API requests.
     */
    String API_REQUEST_LOGGER = "de.ui.api.request";

    /**
     * Logs DE API json requests.
     *
     * <b><u>Suggested Logging Level:</u></b> TRACE
     *
     * <b>NOTE:</b> This logger should only be used as necessary. It should not be turned on by default
     */
    String API_JSON_REQUEST_LOGGER = "de.ui.api.json.request";

    /**
     * Logs DE API json responses.
     *
     * <b><u>Suggested Logging Level:</u></b> TRACE
     *
     * <b>NOTE:</b> This logger should only be used as necessary. It should not be turned on by default
     */
    String API_JSON_RESPONSE_LOGGER = "de.ui.api.json.response";

    /**
     * Logs successful login events.
     */
    String LOGIN = "de.ui.login";

    // MDC Keys
    /**
     * The key used to track the authenticated user in the {@link org.slf4j.MDC}
     */
    String USERNAME_MDC_KEY = "username";

    String RESPONSE_BODY_KEY = "response_body";

    String RESPONSE_ENDPOINT_KEY = "response_endpoint";

    String REQUEST_KEY = "request";
    String REQUEST_ENDPOINT_KEY = "request_endpoint";
    String REQUEST_METHOD_KEY = "request_method";
    String REQUEST_BODY_KEY = "request_body";


}
