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
     * <b>NOTE:</b> This logger should only be used as necessary. It should not be turned on by
     * default.
     *
     * TODO: Possibly augment this logger s.t. specific endpoints can be monitored.
     * For instance, turning on logging for 'org.iplantc.de.api.json.apps` will only reflect
     * activity on the 'apps' endpoint.
     */
    String API_JSON = "org.iplantc.de.api.json";
}
