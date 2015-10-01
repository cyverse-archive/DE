package org.iplantc.de.server;

import static org.iplantc.de.server.AppLoggerConstants.USERINFO_KEY;
import static org.iplantc.de.server.AppLoggerConstants.USER_IP_KEY;

import com.google.common.base.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * This filter is responsible for removing the authenticated username to the logging
 * {@link MDC}. Username is also added to the {@code MDC} in
 * {@link org.iplantc.de.server.auth.AuthenticationSuccessListener}, this is intentional.
 *
 * @author jstroot
 */
public class MDCFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(MDCFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            final HttpServletRequestWrapper servletRequestWrapper = (HttpServletRequestWrapper) servletRequest;
            Map<String, Object> userMap = AppLoggerUtil.getInstance().createUserInfoMap((CasAuthenticationToken) authentication);
            ObjectMapper mapper = new ObjectMapper();
            // This put is also performed in the ApplicationAuthenticationListener. This is intentional.
            MDC.put(USERINFO_KEY, mapper.writeValueAsString(userMap));

            // Look for NGINX ip header. If it doesn't exist, use the default.
            String remoteIP = servletRequestWrapper.getHeader(AppLoggerConstants.USER_IP_HEADER_NAME);
            if(Strings.isNullOrEmpty(remoteIP)) {
                remoteIP = servletRequest.getRemoteAddr();
            }
            MDC.put(USER_IP_KEY, remoteIP);

//            logHeaders(servletRequestWrapper);

        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if(authentication != null) {
                MDC.remove(USERINFO_KEY);
                MDC.remove(USER_IP_KEY);
            }
        }
    }

    /**
     * This method adds the request's header names/values to the MDC if trace-level debugging is on
     * for this class.
     * @param servletRequestWrapper
     */
    private void logHeaders(HttpServletRequestWrapper servletRequestWrapper){
          if(LOG.isTraceEnabled()) {
                final Enumeration<String> headerNames = servletRequestWrapper.getHeaderNames();
                for (; headerNames.hasMoreElements(); ) {
                    final String headerName = headerNames.nextElement();
                    final String headerValue = servletRequestWrapper.getHeader(headerName);
                    MDC.put(AppLoggerConstants.REQUEST_HEADER_KEY + "." + headerName, headerValue);
                }
            }
    }

    @Override
    public void destroy() { }
}
