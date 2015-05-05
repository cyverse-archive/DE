package org.iplantc.de.server;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This filter is responsible for adding/removing the authenticated username to the logging
 * {@link MDC}.
 *
 * @author jstroot
 */
public class MDCFilter implements Filter {

    private final String USERNAME_MDC_KEY = "username";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            MDC.put(USERNAME_MDC_KEY, authentication.getName());
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if(authentication != null) {
                MDC.remove(USERNAME_MDC_KEY);
            }
        }
    }

    @Override
    public void destroy() { }
}
