package org.iplantc.de.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * A filter set no cache headers to GWT .nocache files, and turns caching on for .cache. files.
 * 
 * @author sriram
 * @author jstroot
 * 
 */
public class CacheControlFilter implements Filter {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.contains(".nocache.")) {
            Date now = new Date();
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setDateHeader("Date", now.getTime());
            // one day old
            httpResponse.setDateHeader("Expires", now.getTime() - 86400000L);
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        }else if(requestURI.contains(".cache.")){
            HttpServletResponse httpResponse = (HttpServletResponse)response;

            httpResponse.setHeader("Cache-control", "cache, private, max-age=2592000");
            httpResponse.setHeader("Pragma", "cache");
        }

        filterChain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }

}
