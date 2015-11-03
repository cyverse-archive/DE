package org.iplantc.de.server.rpc;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.google.gwt.logging.shared.RemoteLoggingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogRecord;

/**
 * This class is a light copy of {@link com.google.gwt.logging.server.RemoteLoggingServiceImpl}.
 * This class is necessary for integration of remote logging with Spring MVC.
 *
 * @author jstroot
 * @see org.iplantc.de.conf.GwtRpcConfig
 */
public class DeGwtRemoteLoggingServiceImpl implements RemoteLoggingService {
    private static Logger logger = LoggerFactory.getLogger(RemoteLoggingService.class);
    // No deobfuscator by default
    private StackTraceDeobfuscator deobfuscator = null;
    private String loggerNameOverride = null;

    @Override
    public String logOnServer(LogRecord record) {
        String strongName = null;
        try {
            RemoteLoggingServiceUtil.logOnServer(record, strongName, deobfuscator, loggerNameOverride);
        } catch (RemoteLoggingServiceUtil.RemoteLoggingException e) {
            logger.error("Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        }
        return null;
    }
}
