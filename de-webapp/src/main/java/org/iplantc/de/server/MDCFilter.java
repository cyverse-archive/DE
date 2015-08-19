package org.iplantc.de.server;

import static org.iplantc.de.server.AppLoggerConstants.USERNAME_MDC_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Enumeration;

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
            // This put is also performed in the ApplicationAuthenticationListener. This is intentional.
            MDC.put(USERNAME_MDC_KEY, authentication.getName());

            logHeaders((HttpServletRequestWrapper) servletRequest);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if(authentication != null) {
                MDC.remove(USERNAME_MDC_KEY);
            }
        }
    }

    private void logHeaders(HttpServletRequestWrapper servletRequestWrapper){
          if(LOG.isTraceEnabled()) {
                final Enumeration<String> headerNames = servletRequestWrapper.getHeaderNames();
                String out = "";
                for (; headerNames.hasMoreElements(); ) {
                    final String s = headerNames.nextElement();
                    out += s + " = " + servletRequestWrapper.getHeader(s)+ ", ";
                }
                LOG.trace("HEADERS: {}", out);
            }
    }

    @Override
    public void destroy() { }
}
