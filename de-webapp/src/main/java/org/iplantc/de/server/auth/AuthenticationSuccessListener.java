package org.iplantc.de.server.auth;

import static org.iplantc.de.server.AppLoggerConstants.USERNAME_MDC_KEY;
import org.iplantc.de.server.AppLoggerConstants;

import com.google.common.base.Strings;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Logs successful authentication. The authenticated user is added to the {@link org.slf4j.MDC} both
 * here and in {@link org.iplantc.de.server.MDCFilter}. This event is fired before the filter, and
 * the username wouldn't be in the {@code MDC} unless added here. However, this event only happens
 * once, and the filter is fired many times (during each execution of the security chain), so it is
 * necessary to add the user to the {@code MDC} if it is to show up in log messages.
 *
 * @author jstroot
 */
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final Logger LOGIN_LOG = LoggerFactory.getLogger(AppLoggerConstants.LOGIN);

    private @Autowired HttpServletRequest request;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        MDC.put(USERNAME_MDC_KEY, event.getAuthentication().getName());

        // Look for NGINX ip header. If it doesn't exist, use the default.
        String remoteIP = request.getHeader(AppLoggerConstants.USER_IP_HEADER_NAME);
        if(Strings.isNullOrEmpty(remoteIP)) {
            remoteIP = request.getRemoteAddr();
        }
        MDC.put(AppLoggerConstants.USER_IP_KEY, remoteIP);

        LOGIN_LOG.info("Login success");
    }
}
