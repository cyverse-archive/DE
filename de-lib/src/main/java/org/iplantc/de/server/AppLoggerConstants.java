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
     * Tracks the applications DE API requests for metrics.
     */
    String API_METRICS_LOGGER = "de.ui.api.metrics";

    /**
     * Logs successful login events.
     */
    String LOGIN = "de.ui.login";

    // MDC Keys
    /**
     * The key used to track the authenticated user in the {@link org.slf4j.MDC}
     */
    String USERNAME_MDC_KEY = "user";

    String REQUEST_RESPONSE_BODY_KEY = "request.responseBody";

    String RESPONSE_ENDPOINT_KEY = "response_endpoint";

    String REQUEST_KEY = "request";
    String REQUEST_ENDPOINT_KEY = "request.endpoint";
    String REQUEST_METHOD_KEY = "request.method";
    String REQUEST_BODY_KEY = "request.body";

    String REQUEST_UUID_KEY = "request.uuid";
}
