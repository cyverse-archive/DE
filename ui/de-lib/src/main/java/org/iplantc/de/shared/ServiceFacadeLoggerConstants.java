package org.iplantc.de.shared;

/**
 * Contains logger names and constants used to identify and parse certain client-side log records.
 *
 * Primarily, this is intended to facilitate metrics gathering.
 * Created by jstroot on 8/26/15.
 * @author jstroot
 */
public interface ServiceFacadeLoggerConstants {

    /**
     * Used to communicate the intended Elasticsearch type
     * for event cloning in Logstash.
     */
    String METRIC_TYPE_KEY = "metric_type";

    String APP_EVENT = "app_event";
    String APP_ID = "app.id";

    String ANALYSIS_ID = "analysis.id";
    String ANALYSIS_NAME = "analysis.name";
    String ANALYSIS_OUTPUT_DIR = "analysis.output_dir";

    String SHARE_EVENT = "share_event";
}
