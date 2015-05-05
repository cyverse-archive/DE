package org.iplantc.de.server;

/**
 * This interface holds constants for various loggers used throughout the application.
 * Created by jstroot on 4/30/15.
 * @author jstroot
 */
public interface AppLoggerConstants {
    /**
     * Tracks the applications DE API usage.
     */
    String API_LOGGER = "org.iplantc.de.api";

    /**
     * Logs json requests/responses of the DE API activity.
     *
     * <b><u>Suggested Logging Level:</u></b> TRACE
     *
     * Each endpoint has it's own logger, and this logger is the greatest ancestor.
     * For instance, turning on logging for 'org.iplantc.de.api.json.apps` will only reflect
     * activity on the 'apps' endpoint.
     *
     * <b>NOTE:</b> This logger should only be used as necessary. It should not be turned on by
     * default.
     */
    String API_JSON = "org.iplantc.de.api.json";

    /**
     * Logs successful login events.
     */
    String LOGIN = "org.iplantc.de.login";

    /**
     * The key used to track the authenticated user in the {@link org.slf4j.MDC}
     */
    String USERNAME_MDC_KEY = "username";
}
