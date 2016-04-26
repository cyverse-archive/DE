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
    String USERINFO_KEY = "user-info";

    String RESPONSE_KEY = "response";

    String REQUEST_KEY = "request";

    String USER_IP_KEY = "clientip";

    String USER_IP_HEADER_NAME = "x-real-ip";
    String FWDED_FOR_IP_HEADER_NAME = "x-forwarded-for";
    String REQUEST_ID_HEADER = "X-DE-request-id";
    String REQUEST_ID_HEADER_FWD = "X-DE-forwarded-request-id";
}
