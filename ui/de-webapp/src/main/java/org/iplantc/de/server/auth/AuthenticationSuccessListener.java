package org.iplantc.de.server.auth;

import static org.iplantc.de.server.AppLoggerConstants.LOGIN;
import static org.iplantc.de.server.AppLoggerConstants.USERINFO_KEY;
import static org.iplantc.de.server.AppLoggerConstants.USER_IP_HEADER_NAME;
import static org.iplantc.de.server.AppLoggerConstants.USER_IP_KEY;
import org.iplantc.de.server.AppLoggerUtil;

import com.google.common.base.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    private final Logger LOGIN_LOG = LoggerFactory.getLogger(LOGIN);
    private final Logger LOG = LoggerFactory.getLogger(AuthenticationSuccessListener.class);

    private @Autowired HttpServletRequest request;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {

        Map<String, Object> userMap = AppLoggerUtil.getInstance().createUserInfoMap((CasAuthenticationToken) event.getAuthentication());
        ObjectMapper mapper = new ObjectMapper();
        try {
            MDC.put(USERINFO_KEY, mapper.writeValueAsString(userMap));
        } catch (JsonProcessingException e) {
            LOG.error("Error logging user info", e);
        }
        // Look for NGINX ip header. If it doesn't exist, use the default.
        String remoteIP = request.getHeader(USER_IP_HEADER_NAME);
        if(Strings.isNullOrEmpty(remoteIP)) {
            remoteIP = request.getRemoteAddr();
        }
        MDC.put(USER_IP_KEY, remoteIP);

        LOGIN_LOG.info("Login success");
    }
}
